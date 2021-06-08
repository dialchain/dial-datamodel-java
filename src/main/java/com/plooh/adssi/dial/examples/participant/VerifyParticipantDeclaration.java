package com.plooh.adssi.dial.examples.participant;

import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.plooh.adssi.dial.crypto.Ed25519VerificationKey2021Service;
import com.plooh.adssi.dial.crypto.JcsBase64Ed25519Signature2021Service;
import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.data.VerificationMethod;
import com.plooh.adssi.dial.json.JSON;
import com.plooh.adssi.dial.parser.DialRecordMap;

public class VerifyParticipantDeclaration {
    public boolean handle(String dialRecordString) throws JsonProcessingException {
        DialRecordMap drm = DialRecordMap.parse(dialRecordString);
        ParticipantDeclaration declaration = (ParticipantDeclaration) drm.entries().get(0);
        VerificationMethod verificationMethod = declaration.getVerificationMethod().get(0);
        OctetKeyPair publicKey = Ed25519VerificationKey2021Service
                .publicKeyFromMultibase(verificationMethod.getPublicKeyMultibase(), verificationMethod.getId());
        String declarationString = drm.declarationString();
        Proof proof = drm.proof().get(0);
        HashMap<String, Object> headerParams = JSON.MAPPER.convertValue(proof,
                JcsBase64Ed25519Signature2021Service.typeRefMap);
        return JcsBase64Ed25519Signature2021Service.verify(declarationString, publicKey, headerParams);
    }

}