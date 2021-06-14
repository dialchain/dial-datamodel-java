package com.plooh.adssi.dial.crypto;

import com.nimbusds.jose.jwk.JWK;

import io.ipfs.multibase.Multibase;

public abstract class CommonCurveKeyService {

    public abstract String publicKeyMultibase(JWK publicJWK, Multibase.Base base);

    public abstract JWK publicKeyFromMultibase(String publicKeyMultibase, String keyID);

}