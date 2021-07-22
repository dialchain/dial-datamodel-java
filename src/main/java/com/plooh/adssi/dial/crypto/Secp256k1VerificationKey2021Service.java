package com.plooh.adssi.dial.crypto;

import java.security.Security;
import java.util.List;

import com.plooh.adssi.dial.data.ECKeyPair;
import com.plooh.adssi.dial.data.ECPublicKey;
import com.plooh.adssi.dial.encode.Base64URL;

import org.bitcoinj.core.ECKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import io.ipfs.multibase.Multibase;

public class Secp256k1VerificationKey2021Service extends CommonCurveKeyService<ECKeyPair, ECPublicKey> {
    public static final String KEY_ID_SUFFIX = "key-Secp256k1-";
    public static final String VERIFICATION_METHOD_TYPE = "Secp256k1VerificationKey2021";
    public static final String CURVE = "secp256k1";
    public static final String KEY_USE = "sig";

    public static final Secp256k1VerificationKey2021Service instance = new Secp256k1VerificationKey2021Service();
    public static final BouncyCastleProvider bcProv = new BouncyCastleProvider();
    static {
        Security.addProvider(bcProv);
    }

    private Secp256k1VerificationKey2021Service() {
    }

    @Override
    public ECKeyPair genKeyPair(String keyID) {
        ECKey privateKey = new ECKey();
        ECPublicKey ecPublicKey = ECPublicKey.builder().curve("secp256k1").kid(keyID).keyUse("sig")
                .w(Base64URL.encode_base64Url_utf8_nopad(privateKey.getPubKey())).build();
        return ECKeyPair.builder().d(Base64URL.encode_base64Url_utf8_nopad(privateKey.getPrivKeyBytes()))
                .publicKey(ecPublicKey).build();
    }

    public List<ECKeyPair> keyPairs(int qty, String did) {
        return keyPairs(qty, 0, did);
    }

    public List<ECKeyPair> keyPairs(int qty, int startIndex, String did) {
        return keyPairs(qty, startIndex, did, KEY_ID_SUFFIX);
    }

    @Override
    protected byte[] getPublicKeyBytes(ECPublicKey publicKey) {
        return Base64URL.decode_pad_utf8_base64Url(publicKey.getW());
    }

    @Override
    protected byte[] getPrivateKeyBytes(ECKeyPair privateKey) {
        return Base64URL.decode_pad_utf8_base64Url(privateKey.getD());
    }

    public ECPublicKey ecPublicKey(String publicKeyMultibase, String keyId) {
        String publicKey = Base64URL.encode_base64Url_utf8_nopad(Multibase.decode(publicKeyMultibase));
        return ECPublicKey.builder().curve("secp256k1").kid(keyId).keyUse("sig").w(publicKey).build();
    }

}
