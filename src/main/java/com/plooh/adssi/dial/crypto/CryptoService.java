package com.plooh.adssi.dial.crypto;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.KeyUse;

public class CryptoService {
    public static final JcsBase64Ed25519Signature2021Service ed25519SignatureService = new JcsBase64Ed25519Signature2021Service();
    public static final JcsBase64Spec256k1Signature2021Service spec256k1SignatureService = new JcsBase64Spec256k1Signature2021Service();
    private static final Common25519Service ed25519KeyService = new Common25519Service(Curve.Ed25519, KeyUse.SIGNATURE);
    private static final CommonECKeyService spec256k1KeyService = new CommonECKeyService(Curve.SECP256K1,
            KeyUse.SIGNATURE);

    public static CommonCurveKeyService findKeyService(String keyType) {
        if (Ed25519VerificationKey2021Service.KEY_TYPE.equals(keyType))
            return ed25519KeyService;
        if (Spec256k1VerificationKey2021Service.KEY_TYPE.equals(keyType))
            return spec256k1KeyService;

        throw new IllegalArgumentException("No key service for key type: " + keyType);
    }

    public static JcsBase64EcSignature2021Service findSignatureService(String signatureType) {
        if (JcsBase64Ed25519Signature2021Service.SIGNATURE_TYPE.equals(signatureType))
            return ed25519SignatureService;
        if (JcsBase64Spec256k1Signature2021Service.SIGNATURE_TYPE.equals(signatureType))
            return spec256k1SignatureService;

        throw new IllegalArgumentException("No signature service for signature type: " + signatureType);
    }

    public static JcsBase64EcSignature2021Service findSignatureServiceForKey(String keyType) {
        if (Ed25519VerificationKey2021Service.KEY_TYPE.equals(keyType))
            return ed25519SignatureService;
        if (Spec256k1VerificationKey2021Service.KEY_TYPE.equals(keyType))
            return spec256k1SignatureService;

        throw new IllegalArgumentException("No signature service for key type: " + keyType);
    }
}