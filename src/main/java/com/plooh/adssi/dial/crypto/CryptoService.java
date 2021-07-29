package com.plooh.adssi.dial.crypto;

import com.plooh.adssi.dial.crypto.dial.Neighborhood;

public class CryptoService {
    public static final JcsBase64Ed25519Signature2021Service ed25519SignatureService = new JcsBase64Ed25519Signature2021Service();
    public static final JcsBase64Secp256k1Signature2021Service secp256k1SignatureService = new JcsBase64Secp256k1Signature2021Service();
    public static final Ed25519VerificationKey2021Service ed25519KeyService = Ed25519VerificationKey2021Service.instance;
    public static final X25519KeyAgreementKey2021Service x25519KeyService = X25519KeyAgreementKey2021Service.instance;
    public static final Secp256k1VerificationKey2021Service secp256k1KeyService = Secp256k1VerificationKey2021Service.instance;
    public static final JcsBase64Ed25519JWS2021Service ed25519JwsService = new JcsBase64Ed25519JWS2021Service();
    public static final X25519JweService x25519JweService = new X25519JweService();
    public static final Neighborhood enp = Neighborhood.enp;

    public static CommonCurveKeyService findKeyService(String keyType) {
        if (Ed25519VerificationKey2021Service.VERIFICATION_METHOD_TYPE.equals(keyType))
            return ed25519KeyService;
        if (Secp256k1VerificationKey2021Service.VERIFICATION_METHOD_TYPE.equals(keyType))
            return secp256k1KeyService;
        if (X25519KeyAgreementKey2021Service.VERIFICATION_METHOD_TYPE.equals(keyType))
            return x25519KeyService;

        throw new IllegalArgumentException("No key service for key type: " + keyType);
    }

    public static CommonECSignature2021Service findSignatureService(String signatureType) {
        if (JcsBase64Ed25519Signature2021Service.SIGNATURE_TYPE.equals(signatureType))
            return ed25519SignatureService;
        if (JcsBase64Secp256k1Signature2021Service.SIGNATURE_TYPE.equals(signatureType))
            return secp256k1SignatureService;

        throw new IllegalArgumentException("No signature service for signature type: " + signatureType);
    }

    public static CommonECSignature2021Service findSignatureServiceForKey(String keyType) {
        if (Ed25519VerificationKey2021Service.VERIFICATION_METHOD_TYPE.equals(keyType))
            return ed25519SignatureService;
        if (Secp256k1VerificationKey2021Service.VERIFICATION_METHOD_TYPE.equals(keyType))
            return secp256k1SignatureService;

        throw new IllegalArgumentException("No signature service for key type: " + keyType);
    }
}