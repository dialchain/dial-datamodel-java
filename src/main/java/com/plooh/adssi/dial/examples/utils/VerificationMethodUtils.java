package com.plooh.adssi.dial.examples.utils;

import com.plooh.adssi.dial.crypto.Ed25519VerificationKey2021Service;
import com.plooh.adssi.dial.crypto.Secp256k1VerificationKey2021Service;
import com.plooh.adssi.dial.data.ECKeyPair;
import com.plooh.adssi.dial.data.EncodedECPublicKey;
import com.plooh.adssi.dial.data.OctetKeyPair;
import com.plooh.adssi.dial.data.VerificationMethod;
import com.plooh.adssi.dial.examples.participant.VerificationMethodData;

import io.ipfs.multibase.Multibase.Base;

public class VerificationMethodUtils {

    static final Ed25519VerificationKey2021Service ed25519KeyService = Ed25519VerificationKey2021Service.instance;
    static final Secp256k1VerificationKey2021Service secp256k1KeyService = Secp256k1VerificationKey2021Service.instance;

    public static VerificationMethodData verificationMethodEd25519(String participantId, String creationDate,
            int index) {
        VerificationMethod verificationMethod = new VerificationMethod();
        verificationMethod.setId(participantId + "#" + creationDate + "#key-" + index);
        verificationMethod.setType(Ed25519VerificationKey2021Service.VERIFICATION_METHOD_TYPE);
        OctetKeyPair keyPair = ed25519KeyService.genKeyPair(verificationMethod.getId());
        EncodedECPublicKey publicKeyEnncoded = ed25519KeyService.publicKeyEnncoded(keyPair.getPublicKey());
        String publicKeyMultibase = ed25519KeyService.publicKeyMultibase(publicKeyEnncoded, Base.Base58BTC);
        verificationMethod.setPublicKeyMultibase(publicKeyMultibase);
        if (participantId == null) {
            participantId = publicKeyMultibase;
            verificationMethod.setId(participantId + "#" + creationDate + "#key-" + index);
        }
        return new VerificationMethodData(verificationMethod.getId(),
                ed25519KeyService.ecKeyEncoded(keyPair, keyPair.getPublicKey()), verificationMethod);
    }

    public static VerificationMethodData verificationMethodSecp256k1(String participantId, String creationDate,
            int index) {
        VerificationMethod verificationMethod = new VerificationMethod();
        verificationMethod.setId(participantId + "#" + creationDate + "#key-" + index);
        verificationMethod.setType(Secp256k1VerificationKey2021Service.VERIFICATION_METHOD_TYPE);
        ECKeyPair keyPair = secp256k1KeyService.genKeyPair(verificationMethod.getId());
        EncodedECPublicKey publicKeyEnncoded = secp256k1KeyService.publicKeyEnncoded(keyPair.getPublicKey());
        String publicKeyMultibase = secp256k1KeyService.publicKeyMultibase(publicKeyEnncoded, Base.Base58BTC);
        verificationMethod.setPublicKeyMultibase(publicKeyMultibase);
        return new VerificationMethodData(verificationMethod.getId(),
                secp256k1KeyService.ecKeyEncoded(keyPair, keyPair.getPublicKey()), verificationMethod);
    }
}