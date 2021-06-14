package com.plooh.adssi.dial.examples.participant;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.plooh.adssi.dial.crypto.CryptoService;
import com.plooh.adssi.dial.crypto.Ed25519VerificationKey2021Service;
import com.plooh.adssi.dial.crypto.Spec256k1VerificationKey2021Service;
import com.plooh.adssi.dial.data.AddressType;
import com.plooh.adssi.dial.data.Declarations;
import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.data.ProofPurpose;
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
        String participantId = AddressType.uuid.normalize(UUID.randomUUID().toString());
        participantDeclaration.setId(participantId);
        participantDeclaration.setController(Arrays.asList(participantId));

        participantDeclaration.setVerificationMethod(new ArrayList<>());
        VerificationMethodData verif_ed25519 = verificationMethodEd25519(participantId, creationDate, 0);
        participantDeclaration.getVerificationMethod().add(verif_ed25519.getVerificationMethod());

        VerificationMethodData verif_spec2561 = verificationMethodSpec256k1(participantId, creationDate, 1);
        participantDeclaration.getVerificationMethod().add(verif_spec2561.getVerificationMethod());

        SignatureAssertionMethod signatureAssertionMethod = new SignatureAssertionMethod(
                participantId + "#" + creationDate + "#am-0");
        signatureAssertionMethod.setVerificationMethod(verif_ed25519.getId());
        participantDeclaration.setAssertionMethod(Arrays.asList(signatureAssertionMethod));

        Proof ed25519Proof = new Proof();
        ed25519Proof.setDocument(declarations.getId());
        ed25519Proof.setIssuer(participantId);
        ed25519Proof.setProofPurpose(ProofPurpose.PoP.name());
        ed25519Proof.setVerificationMethod(verif_ed25519.getVerificationMethod().getId());
        ed25519Proof.setCreated(creationDate);
        ed25519Proof.setNonce(UUID.randomUUID().toString());
        String signedRecord = CryptoService.ed25519SignatureService.sign(JSON.MAPPER.writeValueAsString(declarations),
                verif_ed25519.getKeyPair(), ed25519Proof);

        Proof spec256k1Proof = new Proof();
        spec256k1Proof.setDocument(declarations.getId());
        spec256k1Proof.setIssuer(participantId);
        spec256k1Proof.setProofPurpose(ProofPurpose.PoP.name());
        spec256k1Proof.setVerificationMethod(verif_spec2561.getVerificationMethod().getId());
        spec256k1Proof.setCreated(creationDate);
        spec256k1Proof.setNonce(UUID.randomUUID().toString());
        signedRecord = CryptoService.spec256k1SignatureService.sign(signedRecord, verif_spec2561.getKeyPair(),
                spec256k1Proof);

        NewParticipantDeclaration result = new NewParticipantDeclaration();
        result.setRecord(signedRecord);
        result.getVerificationMethod().put(verif_ed25519.getId(), verif_ed25519);
        result.getVerificationMethod().put(verif_spec2561.getId(), verif_spec2561);

        return result;
    }

    public VerificationMethodData verificationMethodEd25519(String participantId, String creationDate, int index) {
        VerificationMethod verificationMethod = new VerificationMethod();
        verificationMethod.setId(participantId + "#" + creationDate + "#key-" + index);
        verificationMethod.setType(Ed25519VerificationKey2021Service.KEY_TYPE);
        OctetKeyPair keyPair = Ed25519VerificationKey2021Service.generateKeyPair(verificationMethod.getId());
        String publicKeyMultibase = Ed25519VerificationKey2021Service.publicKeyMultibase(keyPair.toPublicJWK());
        verificationMethod.setPublicKeyMultibase(publicKeyMultibase);
        return new VerificationMethodData(verificationMethod.getId(), keyPair, verificationMethod);
    }

    public VerificationMethodData verificationMethodSpec256k1(String participantId, String creationDate, int index) {
        VerificationMethod verificationMethod = new VerificationMethod();
        verificationMethod.setId(participantId + "#" + creationDate + "#key-" + index);
        verificationMethod.setType(Spec256k1VerificationKey2021Service.KEY_TYPE);
        ECKey keyPair = Spec256k1VerificationKey2021Service.generateKeyPair(verificationMethod.getId());
        String publicKeyMultibase = Spec256k1VerificationKey2021Service.publicKeyMultibase(keyPair.toPublicJWK());
        verificationMethod.setPublicKeyMultibase(publicKeyMultibase);
        return new VerificationMethodData(verificationMethod.getId(), keyPair, verificationMethod);
    }
}