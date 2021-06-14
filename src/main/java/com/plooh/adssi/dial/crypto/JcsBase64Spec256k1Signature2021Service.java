package com.plooh.adssi.dial.crypto;

import java.security.interfaces.ECPublicKey;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.JWK;

public class JcsBase64Spec256k1Signature2021Service extends JcsBase64EcSignature2021Service {

    static final String SIGNATURE_TYPE = "JcsBase64Spec256k1Signature2021";

    @Override
    public String signatureType() {
        return SIGNATURE_TYPE;
    }

    @Override
    protected JWSSigner jwsSigner(JWK keyPair) throws JOSEException {
        return new ECDSASigner(keyPair.toECKey());
    }

    @Override
    protected JWSVerifier jwsVerifier(JWK publicKey) throws JOSEException {
        return new ECDSAVerifier(publicKey.toECKey().toECPublicKey());
    }

    @Override
    protected JWSAlgorithm jwsAlgorithm() {
        return JWSAlgorithm.ES256K;
    }
}
