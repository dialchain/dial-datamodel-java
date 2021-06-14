package com.plooh.adssi.dial.crypto;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetKeyPair;

public class JcsBase64Ed25519Signature2021Service extends JcsBase64EcSignature2021Service {

    static final String SIGNATURE_TYPE = "JcsBase64Ed25519Signature2021";

    @Override
    public String signatureType() {
        return SIGNATURE_TYPE;
    }

    @Override
    protected JWSSigner jwsSigner(JWK keyPair) throws JOSEException {
        return new Ed25519Signer((OctetKeyPair) keyPair);
    }

    @Override
    protected JWSVerifier jwsVerifier(JWK publicKey) throws JOSEException {
        return new Ed25519Verifier((OctetKeyPair) publicKey);
    }

    @Override
    protected JWSAlgorithm jwsAlgorithm() {
        return JWSAlgorithm.EdDSA;
    }
}
