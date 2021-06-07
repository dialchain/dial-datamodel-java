package com.plooh.adssi.dial.crypto;

import java.util.List;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetKeyPair;

import io.ipfs.multibase.Multibase;

public class Ed25519VerificationKey2021Service {
    public static final String KEY_TYPE = "Ed25519VerificationKey2021";
    private static final Common25519Service ed25519 = new Common25519Service(Curve.Ed25519);

    public static OctetKeyPair generateKeyPair(String keyID) {
        return ed25519.genKeyPair(keyID);
    }

    public static String publicKeyMultibase(OctetKeyPair publicJWK) {
        return ed25519.publicKeyMultibase(publicJWK, Multibase.Base.Base58BTC);
    }

    public static OctetKeyPair publicKeyFromMultibase(String publicKeyMultibase, String keyID) {
        return ed25519.publicKeyFromMultibase(publicKeyMultibase, keyID, KeyUse.SIGNATURE);
    }

    public static List<OctetKeyPair> keyPairs(int qty, String did) {
        return keyPairs(qty, 0, did);
    }

    public static List<OctetKeyPair> keyPairs(int qty, int startIndex, String did) {
        return ed25519.keyPairs(qty, startIndex, did, "#key-Ed25519-");
    }
}
