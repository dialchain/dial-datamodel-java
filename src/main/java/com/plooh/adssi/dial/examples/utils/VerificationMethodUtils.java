package com.plooh.adssi.dial.examples.utils;

import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.plooh.adssi.dial.crypto.Ed25519VerificationKey2021Service;
import com.plooh.adssi.dial.crypto.Secp256k1VerificationKey2021Service;
import com.plooh.adssi.dial.data.VerificationMethod;
import com.plooh.adssi.dial.examples.participant.VerificationMethodData;

public class VerificationMethodUtils {

    public static VerificationMethodData verificationMethodEd25519(String participantId, String creationDate,
            int index) {
        VerificationMethod verificationMethod = new VerificationMethod();
        verificationMethod.setId(participantId + "#" + creationDate + "#key-" + index);
        verificationMethod.setType(Ed25519VerificationKey2021Service.KEY_TYPE);
        OctetKeyPair keyPair = Ed25519VerificationKey2021Service.generateKeyPair(verificationMethod.getId());
        String publicKeyMultibase = Ed25519VerificationKey2021Service.publicKeyMultibase(keyPair.toPublicJWK());
        verificationMethod.setPublicKeyMultibase(publicKeyMultibase);
        if (participantId == null) {
            participantId = publicKeyMultibase;
            verificationMethod.setId(participantId + "#" + creationDate + "#key-" + index);
        }
        return new VerificationMethodData(verificationMethod.getId(), keyPair, verificationMethod);
    }

    public static VerificationMethodData verificationMethodSecp256k1(String participantId, String creationDate,
            int index) {
        VerificationMethod verificationMethod = new VerificationMethod();
        verificationMethod.setId(participantId + "#" + creationDate + "#key-" + index);
        verificationMethod.setType(Secp256k1VerificationKey2021Service.KEY_TYPE);
        ECKey keyPair = Secp256k1VerificationKey2021Service.generateKeyPair(verificationMethod.getId());
        String publicKeyMultibase = Secp256k1VerificationKey2021Service.publicKeyMultibase(keyPair.toPublicJWK());
        verificationMethod.setPublicKeyMultibase(publicKeyMultibase);
        return new VerificationMethodData(verificationMethod.getId(), keyPair, verificationMethod);
    }
}