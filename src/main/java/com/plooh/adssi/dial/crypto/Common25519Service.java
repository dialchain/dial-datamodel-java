package com.plooh.adssi.dial.crypto;

import java.util.ArrayList;
import java.util.List;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import com.nimbusds.jose.util.Base64URL;

import io.ipfs.multibase.Multibase;

public class Common25519Service {
    private final Curve curve;

    public Common25519Service(Curve curve) {
        this.curve = curve;
    }

    public OctetKeyPair genKeyPair(String keyID) {
        try {
            return new OctetKeyPairGenerator(curve).keyID(keyID).generate();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public String publicKeyMultibase(OctetKeyPair publicJWK, Multibase.Base base) {
        byte[] pkBytes = publicJWK.getX().decode();
        return Multibase.encode(base, pkBytes);
    }

    public OctetKeyPair publicKeyFromMultibase(String publicKeyMultibase, String keyID, KeyUse keyUse) {
        byte[] keyBytes = Multibase.decode(publicKeyMultibase);
        Base64URL x = Base64URL.encode(keyBytes);
        return new OctetKeyPair.Builder(curve, x).keyID(keyID).keyUse(keyUse).build();
    }

    public List<OctetKeyPair> keyPairs(int qty, int startIndex, String did, String prefix) {
        List<OctetKeyPair> result = new ArrayList<>();
        for (int i = 0; i < qty; i++) {
            result.add(genKeyPair(did + prefix + (i + startIndex)));
        }
        return result;
    }
}