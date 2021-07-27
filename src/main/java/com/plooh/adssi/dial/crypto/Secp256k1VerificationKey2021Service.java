package com.plooh.adssi.dial.crypto;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import com.plooh.adssi.dial.data.ECKeyPair;
import com.plooh.adssi.dial.data.ECPublicKey;
import com.plooh.adssi.dial.encode.Base64URL;

import org.bitcoinj.core.ECKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import io.ipfs.multibase.Multibase;
import io.ipfs.multibase.Multibase.Base;

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

    public List<ECKeyPair> keyPairs(int qty, String did, String creationDate) {
        return keyPairs(qty, 0, did, creationDate);
    }

    public List<ECKeyPair> keyPairs(int qty, int startIndex, String did, String creationDate) {
        return keyPairs(qty, startIndex, did, creationDate, KEY_ID_SUFFIX);
    }

    public String publicKeyToMultibase(final ECPublicKey ecpk) {
        return Multibase.encode(Base.Base58BTC, Base64URL.decode_pad_utf8_base64Url(ecpk.getW()));
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

    /// Gennerate a keypair and use the public key base58 multibase of the
    /// first key as did
    public List<ECKeyPair> keyPairsAutoPrefixed(int qty, String creationDate, int startIndex)

    {
        final List<ECKeyPair> result = new ArrayList<>();
        if (qty < 1)
            return result;

        final ECKeyPair ecKeyPair = keyPairs(1, startIndex, "no-did", creationDate, KEY_ID_SUFFIX).get(0);
        final String did = publicKeyToMultibase(ecKeyPair.getPublicKey());
        final String key_startIndex_keyId = did + "-" + creationDate + "-" + KEY_ID_SUFFIX + startIndex;
        result.add(_switchKeyId(ecKeyPair, key_startIndex_keyId));
        result.addAll(keyPairs(qty - 1, startIndex + 1, did, creationDate, KEY_ID_SUFFIX));
        return result;
    }

    private ECKeyPair _switchKeyId(ECKeyPair eckp, String keyId) {
        return ECKeyPair.builder().d(eckp.getD()).publicKey(ECPublicKey.builder().w(eckp.getPublicKey().getW())
                .curve(eckp.getPublicKey().getCurve()).keyUse(eckp.getPublicKey().getKeyUse()).kid(keyId).build())
                .build();
    }

    @Override
    public String getType() {
        return VERIFICATION_METHOD_TYPE;
    }
}
