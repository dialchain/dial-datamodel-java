package com.plooh.adssi.dial.crypto;

import java.util.ArrayList;
import java.util.List;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import com.nimbusds.jose.util.Base64URL;

import io.ipfs.multibase.Multibase;

public class Common25519Service extends CommonCurveKeyService {
    private final Curve curve;
    private final KeyUse keyUse;

    public Common25519Service(Curve curve, KeyUse keyUse) {
        this.curve = curve;
        this.keyUse = keyUse;
    }

    public OctetKeyPair genKeyPair(String keyID) {
        try {
            return new OctetKeyPairGenerator(curve).keyID(keyID).generate();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String publicKeyMultibase(JWK publicJWK, Multibase.Base base) {
        OctetKeyPair okp = (OctetKeyPair) publicJWK;
        byte[] pkBytes = okp.getX().decode();
        return Multibase.encode(base, pkBytes);
    }

    @Override
    public JWK publicKeyFromMultibase(String publicKeyMultibase, String keyID) {
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