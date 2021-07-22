package com.plooh.adssi.dial.crypto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.plooh.adssi.dial.data.EncodedECKey;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.data.SignatureResult;
import com.plooh.adssi.dial.data.ValidationResult;
import com.plooh.adssi.dial.data.VerificationMethod;
import com.plooh.adssi.dial.encode.Base64URL;
import com.plooh.adssi.dial.jcs.JCS;
import com.plooh.adssi.dial.json.JSON;
import com.plooh.adssi.dial.lookup.PublicKeyResolver;
import com.plooh.adssi.dial.parser.SignedDocumentMapped;

public abstract class CommonECSignature2021Service {
    public static final String DIAL_001_002_001_signature_invalid = "DIAL_001_002_001_signature_invalid";
    public static final String DIAL_001_002_002_signature_lookup_verificationMethod_failed = "DIAL_001_002_002_signature_lookup_verificationMethod_failed";
    public static final String DIAL_001_002_003_signature_missing_signature_value_in_proof = "DIAL_001_002_003_signature_missing_signature_value_in_proof";
    public static final String DIAL_001_002_004_signature_missing_assertion_or_verificationMethod_in_proof = "DIAL_001_002_004_signature_missing_assertion_or_verificationMethod_in_proof";

    public abstract String signatureType();

    public abstract SignatureResult signDeclaration(String recordJson, Proof template, EncodedECKey keyPair);

    protected Proof prepareProof(Proof template) {
        return Proof.builder().issuer(template.getIssuer()).created(template.getCreated())
                .proofPurpose(template.getProofPurpose()).type(signatureType())
                .assertionMethod(template.getAssertionMethod()).verificationMethod(template.getVerificationMethod())
                .nonce(template.getNonce()).build();
    }

    protected byte[] prepareSignatureInputs(SignedDocumentMapped signedDocumentMapped, Proof signatureProof) {
        // Remove proof block
        String signaturePayload = signedDocumentMapped.deleteProof().toJson();

        // produce signature payload string
        final String jcs_utf8_base64urlData = Base64URL
                .encode_base64Url_utf8_nopad(JCS.encode_utf8((signaturePayload)));
        // Produce the signature header string
        final String jcs_utf8_base64urlHeader = Base64URL
                .encode_base64Url_utf8_nopad(JCS.encode_utf8((JSON.encode(signatureProof))));

        // jws input
        return (jcs_utf8_base64urlHeader + "." + jcs_utf8_base64urlData).getBytes(StandardCharsets.UTF_8);
    }

    protected SignatureResult prepareSignatureResult(byte[] signatureBytes, Proof signatureProof, String recordJson) {
        // Produce signature b64 string
        final String base64encodedSignature = Base64URL.encode_base64Url_utf8_nopad(signatureBytes);

        // Add signatrue to proof
        signatureProof.setSignatureValue(base64encodedSignature);

        // Add the proof to the json object and return document.
        String signnedRecord = new SignedDocumentMapped(recordJson).addProof(signatureProof).toJson();

        // Encode signed document
        return new SignatureResult(signnedRecord, Collections.emptyList());
    }

    /// Verify that the given proof signs the given record.
    /// Returns an empty list upon successful verification.
    public List<ValidationResult> verifyDeclaration(String recordJson, Proof proof, PublicKeyResolver publicKeyResolver)

    {
        final List<ValidationResult> result = new ArrayList<>();

        // Validate and isolate the signature value.
        final String signatureBe64URL = proof.getSignatureValue();
        if (signatureBe64URL == null) {
            result.add(ValidationResult.builder()
                .key(DIAL_001_002_003_signature_missing_signature_value_in_proof)
                .details(Collections.singletonMap("proof", proof)).build());
            return result;
        }

        // Resolve the verification method associated with this proof
        final VerificationMethod verificationMethod = publicKeyResolver.lookup(proof);
        if (verificationMethod == null) {
            result.add(ValidationResult.builder()
                    .key(DIAL_001_002_002_signature_lookup_verificationMethod_failed)
                    .details(Collections.singletonMap("proof", proof)).build());
            return result;
        }

        // Remove the proof block and produce signature payload string
        String signaturePayload = new SignedDocumentMapped(recordJson).deleteProof().toJson();
        final String jcs_utf8_base64urlData = Base64URL.encode_base64Url_utf8_nopad(JCS.encode_utf8(signaturePayload));

        // Remove the signature value from proof and produce the signature header
        // strinng
        proof.setSignatureValue(null);
        final String jcs_utf8_base64urlHeader = Base64URL
                .encode_base64Url_utf8_nopad(JCS.encode_utf8(JSON.encode(proof)));

        // Read public key from verification method.
        final boolean verified = verifySignature(verificationMethod, jcs_utf8_base64urlHeader, jcs_utf8_base64urlData,
                signatureBe64URL);

        if ( !verified ) {
            result.add(ValidationResult.builder()
                .key(DIAL_001_002_001_signature_invalid)
                .details(Map.of("proof", proof, "verificationMethod", verificationMethod))
                .build());
        }

        return result;
    }

    protected abstract boolean verifySignature(VerificationMethod verificationMethod, String jcs_utf8_base64urlHeader,
            String jcs_utf8_base64urlData, String signatureBe64URL);
}
