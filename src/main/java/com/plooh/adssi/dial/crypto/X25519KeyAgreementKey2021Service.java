package com.plooh.adssi.dial.crypto;

import java.security.InvalidKeyException;
import java.util.List;

import com.google.crypto.tink.subtle.X25519;
import com.plooh.adssi.dial.data.OctetKeyPair;
import com.plooh.adssi.dial.data.OctetPublicKey;
import com.plooh.adssi.dial.encode.Base64URL;

public class X25519KeyAgreementKey2021Service extends CommonCurveKeyService<OctetKeyPair, OctetPublicKey> {
    public static final String KEY_ID_SUFFIX = "key-X25519-";
    public static final String VERIFICATION_METHOD_TYPE = "X25519KeyAgreementKey2021";
    public static final String CURVE = "x25519";
    public static final String KEY_USE = "enc";

    public static X25519KeyAgreementKey2021Service instance = new X25519KeyAgreementKey2021Service();

    private X25519KeyAgreementKey2021Service() {
    }

    @Override
    public OctetKeyPair genKeyPair(String keyID) {
        final byte[] privateKeyBytes;
        final byte[] publicKeyBytes;

        try {
            privateKeyBytes = X25519.generatePrivateKey();
            publicKeyBytes = X25519.publicFromPrivate(privateKeyBytes);

        } catch (InvalidKeyException e) {
            // internal Tink error, should not happen
            throw new RuntimeException(e.getMessage(), e);
        }

        String privateKey = Base64URL.encode_base64Url_utf8_nopad(privateKeyBytes);
        String publicKey = Base64URL.encode_base64Url_utf8_nopad(publicKeyBytes);
        OctetPublicKey opk = OctetPublicKey.builder().kid(keyID).keyUse(KEY_USE).curve(CURVE).x(publicKey).build();
        return OctetKeyPair.builder().d(privateKey).publicKey(opk).build();
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

    public byte[] publicKeyFromBase64Url(String publicKey64Url) {
        return Base64URL.decode_pad_utf8_base64Url(publicKey64Url);
    }
}
