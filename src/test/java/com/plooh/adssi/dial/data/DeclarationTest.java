package com.plooh.adssi.dial.data;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.nimbusds.jose.jwk.OctetKeyPair;
import com.plooh.adssi.dial.crypto.Ed25519VerificationKey2021Service;
import com.plooh.adssi.dial.crypto.JcsBase64Ed25519Signature2021Service;
import com.plooh.adssi.dial.utils.JCSUtils;
import com.plooh.adssi.dial.utils.JsonUtils;

import org.junit.jupiter.api.Test;

public class DeclarationTest {
    @Test
    public void testSignVerify() throws IOException {
        DialRecord dr = new DialRecord();
        dr.setDeclaration(new Declarations());
        dr.getDeclaration().setId(AddressType.uuid.normalize(UUID.randomUUID().toString()));
        dr.getDeclaration().setEntries(new ArrayList<>());

        ParticipantDeclaration participantDeclaration = new ParticipantDeclaration();
        dr.getDeclaration().getEntries().add(participantDeclaration);
        String creationDate = "2021-05-12T10:12:00Z";
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

        String dataJson = JsonUtils.MAPPER.writeValueAsString(dr.getDeclaration());

        Proof proof = new Proof();
        proof.setDeclaration(dr.getDeclaration().getId());
        proof.setIssuer(participantDeclaration.getId());
        proof.setAssertionMethod(Arrays.asList(signatureAssertionMethod.getId()));
        proof.setCreated("2021-05-12T10:12:00Z");
        proof.setNonce(UUID.randomUUID().toString());
        HashMap<String, Object> headerParams = JsonUtils.MAPPER.convertValue(proof,
                JcsBase64Ed25519Signature2021Service.typeRefMap);
        Map<String, Object> result = JcsBase64Ed25519Signature2021Service.sign(dataJson, keyPair, headerParams);
        proof = JsonUtils.MAPPER.convertValue(result, Proof.class);

        dr.setProof(Arrays.asList(proof));

        String recordString = JsonUtils.MAPPER.writeValueAsString(dr);
        assertNotNull(dataJson);
    }
}