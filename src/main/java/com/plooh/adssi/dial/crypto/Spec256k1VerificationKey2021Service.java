package com.plooh.adssi.dial.crypto;

import java.util.List;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;

import io.ipfs.multibase.Multibase;

public class Spec256k1VerificationKey2021Service {
    public static final String KEY_TYPE = "Spec256k1VerificationKey2021";

    private static final CommonECKeyService spec256k1 = new CommonECKeyService(Curve.SECP256K1, KeyUse.SIGNATURE);

    public static ECKey generateKeyPair(String keyID) {
        return spec256k1.genKeyPair(keyID);
    }

    public static String publicKeyMultibase(ECKey publicJWK) {
        return spec256k1.publicKeyMultibase(publicJWK, Multibase.Base.Base58BTC);
    }

    public static JWK publicKeyFromMultibase(String publicKeyMultibase, String keyID) {
        return spec256k1.publicKeyFromMultibase(publicKeyMultibase, keyID);
    }

    public static List<ECKey> keyPairs(int qty, String did) {
        return keyPairs(qty, 0, did);
    }

    public static List<ECKey> keyPairs(int qty, int startIndex, String did) {
        return spec256k1.keyPairs(qty, startIndex, did, "#key-spec256k1-");
    }

}
