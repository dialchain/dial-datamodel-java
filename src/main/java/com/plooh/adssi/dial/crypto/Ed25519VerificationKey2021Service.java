package com.plooh.adssi.dial.crypto;

import java.security.GeneralSecurityException;
import java.util.List;

import com.google.crypto.tink.subtle.Ed25519Sign;
import com.google.crypto.tink.subtle.Ed25519Sign.KeyPair;
import com.plooh.adssi.dial.data.OctetKeyPair;
import com.plooh.adssi.dial.data.OctetPublicKey;
import com.plooh.adssi.dial.encode.Base64URL;

import io.ipfs.multibase.Multibase;

public class Ed25519VerificationKey2021Service extends CommonCurveKeyService<OctetKeyPair, OctetPublicKey> {
    public static final String KEY_ID_SUFFIX = "key-Ed25519-";
    public static final String VERIFICATION_METHOD_TYPE = "Ed25519VerificationKey2021";
    public static final String CURVE = "ed25519";
    public static final String KEY_USE = "sig";

    public static Ed25519VerificationKey2021Service instance = new Ed25519VerificationKey2021Service();

    private Ed25519VerificationKey2021Service() {
    }

    @Override
    public OctetKeyPair genKeyPair(String keyID) {
        final KeyPair okp;

        try {
            okp = Ed25519Sign.KeyPair.newKeyPair();
        } catch (GeneralSecurityException e) {
            // internal Tink error, should not happen
            throw new RuntimeException(e.getMessage(), e);
        }

        String privateKey = Base64URL.encode_base64Url_utf8_nopad(okp.getPrivateKey());
        String publicKey = Base64URL.encode_base64Url_utf8_nopad(okp.getPublicKey());
        OctetPublicKey opk = OctetPublicKey.builder().kid(keyID).keyUse(KEY_USE).curve(CURVE).x(publicKey).build();
        return OctetKeyPair.builder().d(privateKey).publicKey(opk).build();
    }

    public OctetPublicKey octetPublicKey(String publicKeyMultibase, String keyId) {
        String publicKey = Base64URL.encode_base64Url_utf8_nopad(Multibase.decode(publicKeyMultibase));
        return OctetPublicKey.builder().kid(keyId).keyUse(KEY_USE).curve(CURVE).x(publicKey).build();
    }

    public List<OctetKeyPair> keyPairs(int qty, int startIndex, String did) {
        return keyPairs(qty, startIndex, did, KEY_ID_SUFFIX);
    }

    public List<OctetKeyPair> keyPairs(int qty, String did) {
        return keyPairs(qty, 0, did);
    }

    @Override
    protected byte[] getPublicKeyBytes(OctetPublicKey publicKey) {
        return Base64URL.decode_pad_utf8_base64Url(publicKey.getX());
    }

    @Override
    protected byte[] getPrivateKeyBytes(OctetKeyPair privateKey) {
        return Base64URL.decode_pad_utf8_base64Url(privateKey.getD());
    }

}
