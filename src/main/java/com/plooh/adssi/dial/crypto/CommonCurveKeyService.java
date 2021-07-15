package com.plooh.adssi.dial.crypto;

import java.util.ArrayList;
import java.util.List;

import com.plooh.adssi.dial.data.EncodedECKey;
import com.plooh.adssi.dial.data.EncodedECPublicKey;

import io.ipfs.multibase.Multibase;

public abstract class CommonCurveKeyService<T, PK> {

    public String publicKeyMultibase(EncodedECPublicKey publicKey, Multibase.Base base) {
        return Multibase.encode(base, publicKey.getBytes());
    }

    public EncodedECPublicKey publicKeyFromMultibase(String publicKeyMultibase, String keyID) {
        return toEncodedECPublicKey(Multibase.decode(publicKeyMultibase));
    }

    public EncodedECPublicKey publicKeyEnncoded(PK publicKey) {
        return toEncodedECPublicKey(getPublicKeyBytes(publicKey));
    }

    public EncodedECKey ecKeyEncoded(T privateKey, PK publicKey) {
        return new EncodedECKey(getPrivateKeyBytes(privateKey), publicKeyEnncoded(publicKey));
    }

    public List<T> keyPairs(int qty, int startIndex, String did, String prefix) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < qty; i++) {
            result.add(genKeyPair(did + prefix + (i + startIndex)));
        }
        return result;
    }

    public abstract T genKeyPair(String keyID);

    protected abstract byte[] getPublicKeyBytes(PK publicKey);

    protected abstract byte[] getPrivateKeyBytes(T privateKey);

    private EncodedECPublicKey toEncodedECPublicKey(byte[] publicKeyBytes) {
        byte[] pubKeyHash = CryptoUtils.sah256Ripemd160(publicKeyBytes);
        return new EncodedECPublicKey(publicKeyBytes, pubKeyHash, true);
    }
}