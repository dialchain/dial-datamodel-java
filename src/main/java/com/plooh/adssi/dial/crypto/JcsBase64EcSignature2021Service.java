package com.plooh.adssi.dial.crypto;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.util.Base64URL;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.jcs.JCS;
import com.plooh.adssi.dial.json.JSON;
import com.plooh.adssi.dial.parser.SignedDocumentMapped;
import com.plooh.adssi.dial.parser.TimeFormat;

public abstract class JcsBase64EcSignature2021Service {

    public abstract String signatureType();

    protected abstract JWSSigner jwsSigner(JWK keyPair) throws JOSEException;

    protected abstract JWSVerifier jwsVerifier(JWK publicKey) throws JOSEException;

    protected abstract JWSAlgorithm jwsAlgorithm();

    public boolean verify(String recordJson, JWK publicJWK, Proof proof) {
        try {
            return verifyInternal(recordJson, publicJWK, proof);
        } catch (JOSEException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String sign(String recordJson, JWK keyPair, Proof proof) {
        try {
            return signInternal(recordJson, keyPair, proof);
        } catch (JsonProcessingException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private String signInternal(String recordJson, JWK keyPair, Proof proof)
            throws JOSEException, JsonProcessingException {

        // Set date if missing
        if (proof.getCreated() == null) {
            proof.setCreated(TimeFormat.format(Instant.now()));
        }

        // If missing assertion method
        if (proof.getAssertionMethod() == null || proof.getAssertionMethod().isEmpty()) {
            if (proof.getVerificationMethod() == null) {
                throw new IllegalStateException("Either assertion method or verification method must be set.");
            }
        }

        // IF missing issuer
        if (proof.getIssuer() == null) {
            throw new IllegalStateException("Missing issuer");
        }

        // set signature type
        proof.setType(signatureType());

        if (proof.getNonce() == null) {
            proof.setNonce(UUID.randomUUID().toString());
        }

        // Signature header is the proof body without signature value
        proof.setSignatureValue(null);
        String jwsHeaderJson = JSON.MAPPER.writeValueAsString(proof);
        JWSHeader jwsHeader = new JWSHeader.Builder(jwsAlgorithm()).build();

        // Remove all proofs from signature payload
        String signaturePayload = new SignedDocumentMapped(recordJson).deleteProof().toJson();

        Base64URL sign = jwsSigner(keyPair).sign(jwsHeader, signingInput(jwsHeaderJson, signaturePayload));
        proof.setSignatureValue(sign.toString());

        // Add the proof to the json object and return document.
        return new SignedDocumentMapped(recordJson).addProof(proof).toJson();
    }

    private boolean verifyInternal(String recordJson, JWK publicKey, Proof proof)
            throws JOSEException, JsonProcessingException {

        String signatureValue = proof.getSignatureValue();
        if (signatureValue == null) {
            throw new IllegalStateException("Missing signature value");
        }

        if (!signatureType().equals(proof.getType())) {
            throw new IllegalStateException("Wrong signature type");
        }

        // Signature header is the proof body without signature value
        proof.setSignatureValue(null);
        String jwsHeaderJson = JSON.MAPPER.writeValueAsString(proof);
        JWSHeader jwsHeader = new JWSHeader.Builder(jwsAlgorithm()).build();

        // Remove all proofs from signature payload
        String signaturePayload = new SignedDocumentMapped(recordJson).deleteProof().toJson();

        return jwsVerifier(publicKey).verify(jwsHeader, signingInput(jwsHeaderJson, signaturePayload),
                Base64URL.from(signatureValue));
    }

    private byte[] signingInput(String jwsHeaderJson, String datJson) {
        String jwsHeaderJCSBase64 = jcs_utf8_base64url(jwsHeaderJson).toString();
        String jwsPayloadString = jcs_utf8_base64url(datJson).toString();
        return (jwsHeaderJCSBase64 + "." + jwsPayloadString).getBytes(StandardCharsets.UTF_8);
    }

    private Base64URL jcs_utf8_base64url(final String input) {
        return Base64URL.encode(JCS.encode(input));
    }
}
