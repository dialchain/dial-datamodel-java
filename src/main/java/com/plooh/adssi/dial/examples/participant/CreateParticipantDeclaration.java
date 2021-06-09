package com.plooh.adssi.dial.examples.participant;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.plooh.adssi.dial.crypto.Ed25519VerificationKey2021Service;
import com.plooh.adssi.dial.crypto.JcsBase64Ed25519Signature2021Service;
import com.plooh.adssi.dial.data.AddressType;
import com.plooh.adssi.dial.data.Declarations;
import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.data.SignatureAssertionMethod;
import com.plooh.adssi.dial.data.VerificationMethod;
import com.plooh.adssi.dial.json.JSON;
import com.plooh.adssi.dial.parser.TimeFormat;

public class CreateParticipantDeclaration {
    public NewParticipantDeclaration handle(Instant dateTime) throws JsonProcessingException {
        String creationDate = TimeFormat.DTF.format(dateTime);
        Declarations declarations = new Declarations();
        declarations.setId(AddressType.uuid.normalize(UUID.randomUUID().toString()));
        declarations.setType("Declaration");
        declarations.setDeclaration(new ArrayList<>());

        ParticipantDeclaration participantDeclaration = new ParticipantDeclaration();
        declarations.getDeclaration().add(participantDeclaration);
        participantDeclaration.setCreated(creationDate);
        participantDeclaration.setId(AddressType.uuid.normalize(UUID.randomUUID().toString()));
        participantDeclaration.setController(Arrays.asList(participantDeclaration.getId()));

        VerificationMethod verificationMethod = new VerificationMethod();
        verificationMethod.setId(participantDeclaration.getId() + "#" + creationDate + "#key-0");
        verificationMethod.setType(Ed25519VerificationKey2021Service.KEY_TYPE);
        OctetKeyPair keyPair = Ed25519VerificationKey2021Service.generateKeyPair(verificationMethod.getId());
        String publicKeyMultibase = Ed25519VerificationKey2021Service.publicKeyMultibase(keyPair.toPublicJWK());
        verificationMethod.setPublicKeyMultibase(publicKeyMultibase);
        participantDeclaration.setVerificationMethod(Arrays.asList(verificationMethod));

        SignatureAssertionMethod signatureAssertionMethod = new SignatureAssertionMethod(
                participantDeclaration.getId() + "#" + creationDate + "#am-0");
        signatureAssertionMethod.setVerificationMethod(verificationMethod.getId());
        participantDeclaration.setAssertionMethod(Arrays.asList(signatureAssertionMethod));

        Proof proof = new Proof();
        proof.setDocument(declarations.getId());
        proof.setIssuer(participantDeclaration.getId());
        proof.setAssertionMethod(Arrays.asList(signatureAssertionMethod.getId()));
        proof.setCreated(creationDate);
        proof.setNonce(UUID.randomUUID().toString());

        String signedRecord = JcsBase64Ed25519Signature2021Service.sign(JSON.MAPPER.writeValueAsString(declarations),
                keyPair, proof);

        return new NewParticipantDeclaration(signedRecord, keyPair);
    }
}