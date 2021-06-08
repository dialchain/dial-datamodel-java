package com.plooh.adssi.dial.examples.participant;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.plooh.adssi.dial.crypto.Ed25519VerificationKey2021Service;
import com.plooh.adssi.dial.crypto.JcsBase64Ed25519Signature2021Service;
import com.plooh.adssi.dial.data.AddressType;
import com.plooh.adssi.dial.data.Declarations;
import com.plooh.adssi.dial.data.DialRecord;
import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.data.SignatureAssertionMethod;
import com.plooh.adssi.dial.data.VerificationMethod;
import com.plooh.adssi.dial.json.JSON;
import com.plooh.adssi.dial.parser.TimeFormat;

public class CreateParticipantDeclaration {
    public NewParticipantDeclaration handle(Instant dateTime) throws JsonProcessingException {
        String creationDate = TimeFormat.DTF.format(dateTime);
        DialRecord dr = new DialRecord();
        dr.setDeclaration(new Declarations());
        dr.getDeclaration().setId(AddressType.uuid.normalize(UUID.randomUUID().toString()));
        dr.getDeclaration().setEntries(new ArrayList<>());

        ParticipantDeclaration participantDeclaration = new ParticipantDeclaration();
        dr.getDeclaration().getEntries().add(participantDeclaration);
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

        String dataJson = JSON.MAPPER.writeValueAsString(dr.getDeclaration());

        Proof proof = new Proof();
        proof.setDeclaration(dr.getDeclaration().getId());
        proof.setIssuer(participantDeclaration.getId());
        proof.setAssertionMethod(Arrays.asList(signatureAssertionMethod.getId()));
        proof.setCreated(creationDate);
        proof.setNonce(UUID.randomUUID().toString());
        HashMap<String, Object> headerParams = JSON.MAPPER.convertValue(proof,
                JcsBase64Ed25519Signature2021Service.typeRefMap);
        Map<String, Object> result = JcsBase64Ed25519Signature2021Service.sign(dataJson, keyPair, headerParams);
        proof = JSON.MAPPER.convertValue(result, Proof.class);

        dr.setProof(Arrays.asList(proof));

        return new NewParticipantDeclaration(JSON.MAPPER.writeValueAsString(dr), keyPair);
    }
}