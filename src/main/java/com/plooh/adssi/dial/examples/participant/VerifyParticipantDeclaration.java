package com.plooh.adssi.dial.examples.participant;

import com.nimbusds.jose.jwk.OctetKeyPair;
import com.plooh.adssi.dial.crypto.Ed25519VerificationKey2021Service;
import com.plooh.adssi.dial.crypto.JcsBase64Ed25519Signature2021Service;
import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.data.VerificationMethod;
import com.plooh.adssi.dial.parser.ParticipantDeclarationMapped;

public class VerifyParticipantDeclaration {
    public boolean handle(String dialRecordString) {
        ParticipantDeclarationMapped doc = new ParticipantDeclarationMapped(dialRecordString);
        Proof proof = doc.proof().get(0);
        ParticipantDeclaration declaration = doc.declarations().get(0);
        VerificationMethod verificationMethod = declaration.getVerificationMethod().get(0);
        OctetKeyPair publicJWK = Ed25519VerificationKey2021Service
                .publicKeyFromMultibase(verificationMethod.getPublicKeyMultibase(), verificationMethod.getId());
        return JcsBase64Ed25519Signature2021Service.verify(dialRecordString, publicJWK, proof);
    }

}