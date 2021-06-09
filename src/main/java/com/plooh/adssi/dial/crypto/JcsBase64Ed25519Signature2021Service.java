package com.plooh.adssi.dial.crypto;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.jcs.JCS;
import com.plooh.adssi.dial.json.JSON;
import com.plooh.adssi.dial.parser.SignedDocumentMapped;
import com.plooh.adssi.dial.parser.TimeFormat;

public class JcsBase64Ed25519Signature2021Service {

    public static final String SIGNATURE_TYPE = "JcsBase64Ed25519Signature2021";

    public static boolean verify(String recordJson, OctetKeyPair publicJWK, Proof proof) {
        try {
            return verifyInternal(recordJson, publicJWK, proof);
        } catch (JOSEException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sign(String recordJson, OctetKeyPair keyPair, Proof proof) {
        try {
            return signInternal(recordJson, keyPair, proof);
        } catch (JsonProcessingException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private static String signInternal(String recordJson, OctetKeyPair keyPair, Proof proof)
            throws JOSEException, JsonProcessingException {

        // Set date if missing
        if (proof.getCreated() == null) {
            proof.setCreated(TimeFormat.DTF.format(Instant.now()));
        }

        // If missing assertion method
        if (proof.getAssertionMethod() == null || proof.getAssertionMethod().isEmpty()) {
            throw new IllegalStateException("Missing assertion method");
        }

        // IF missing issuer
        if (proof.getIssuer() == null) {
            throw new IllegalStateException("Missing issuer");
        }

        // set signature type
        proof.setType(SIGNATURE_TYPE);

        if (proof.getNonce() == null) {
            proof.setNonce(UUID.randomUUID().toString());
        }

        // Signature header is the proof body without signature value
        proof.setSignatureValue(null);
        String jwsHeaderJson = JSON.MAPPER.writeValueAsString(proof);
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.EdDSA).build();

        // Remove all proofs from signature payload
        String signaturePayload = new SignedDocumentMapped(recordJson).deleteProof().toJson();

        Ed25519Signer ed25519Signer = new Ed25519Signer(keyPair);
        Base64URL sign = ed25519Signer.sign(jwsHeader, signingInput(jwsHeaderJson, signaturePayload));
        proof.setSignatureValue(sign.toString());

        // Add the proof to the json object and return document.
        return new SignedDocumentMapped(recordJson).addProof(proof).toJson();
    }

    private static boolean verifyInternal(String recordJson, OctetKeyPair publicKey, Proof proof)
            throws JOSEException, JsonProcessingException {

        String signatureValue = proof.getSignatureValue();
        if (signatureValue == null) {
            throw new IllegalStateException("Missing signature value");
        }

        if (!SIGNATURE_TYPE.equals(proof.getType())) {
            throw new IllegalStateException("Wrong signature type");
        }

        // Signature header is the proof body without signature value
        proof.setSignatureValue(null);
        String jwsHeaderJson = JSON.MAPPER.writeValueAsString(proof);
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.EdDSA).build();

        // Remove all proofs from signature payload
        String signaturePayload = new SignedDocumentMapped(recordJson).deleteProof().toJson();

        Ed25519Verifier ed25519Verifier = new Ed25519Verifier(publicKey);
        return ed25519Verifier.verify(jwsHeader, signingInput(jwsHeaderJson, signaturePayload),
                Base64URL.from(signatureValue));
    }

    private static byte[] signingInput(String jwsHeaderJson, String datJson) {
        String jwsHeaderJCSBase64 = jcs_utf8_base64url(jwsHeaderJson).toString();
        String jwsPayloadString = jcs_utf8_base64url(datJson).toString();
        return (jwsHeaderJCSBase64 + "." + jwsPayloadString).getBytes(StandardCharsets.UTF_8);
    }

    private static Base64URL jcs_utf8_base64url(final String input) {
        return Base64URL.encode(JCS.encode(input));
    }
}
