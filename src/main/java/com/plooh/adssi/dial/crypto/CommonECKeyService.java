package com.plooh.adssi.dial.crypto;

import java.util.ArrayList;
import java.util.List;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.util.Base64URL;

import io.ipfs.multibase.Multibase;

public class CommonECKeyService extends CommonCurveKeyService {
    private final Curve curve;
    private final KeyUse keyUse;

    public CommonECKeyService(Curve curve, KeyUse keyUse) {
        this.curve = curve;
        this.keyUse = keyUse;
    }

    public ECKey genKeyPair(String keyID) {
        try {
            return new ECKeyGenerator(curve).keyUse(keyUse).keyID(keyID).generate();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String publicKeyMultibase(JWK publicJWK, Multibase.Base base) {
        ECKey ecKey = (ECKey) publicJWK;
        byte[] xBytes = ecKey.getX().decode();
        byte[] yBytes = ecKey.getY().decode();
        byte[] c = new byte[xBytes.length + yBytes.length];
        System.arraycopy(xBytes, 0, c, 0, xBytes.length);
        System.arraycopy(yBytes, 0, c, xBytes.length, yBytes.length);
        return Multibase.encode(base, c);
    }

    @Override
    public JWK publicKeyFromMultibase(String publicKeyMultibase, String keyID) {
        byte[] keyBytes = Multibase.decode(publicKeyMultibase);
        byte[] xBytes = new byte[keyBytes.length / 2];
        byte[] yBytes = new byte[keyBytes.length / 2];
        System.arraycopy(keyBytes, 0, xBytes, 0, keyBytes.length / 2);
        System.arraycopy(keyBytes, keyBytes.length / 2, yBytes, 0, keyBytes.length / 2);
        Base64URL x = Base64URL.encode(xBytes);
        Base64URL y = Base64URL.encode(yBytes);
        return new ECKey.Builder(curve, x, y).keyID(keyID).keyUse(keyUse).build();
    }

    public List<ECKey> keyPairs(int qty, int startIndex, String did, String prefix) {
        List<ECKey> result = new ArrayList<>();
        for (int i = 0; i < qty; i++) {
            result.add(genKeyPair(did + prefix + (i + startIndex)));
        }
        return result;
    }
}