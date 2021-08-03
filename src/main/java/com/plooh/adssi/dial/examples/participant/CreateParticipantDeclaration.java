package com.plooh.adssi.dial.examples.participant;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plooh.adssi.dial.crypto.CryptoService;
import com.plooh.adssi.dial.data.Declarations;
import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.data.PerformanceDeclaration;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.data.ProofPurpose;
import com.plooh.adssi.dial.data.Service;
import com.plooh.adssi.dial.data.VerificationMethod;
import com.plooh.adssi.dial.examples.utils.VerificationMethodUtils;
import com.plooh.adssi.dial.json.JSON;
import com.plooh.adssi.dial.parser.TimeFormat;

public class CreateParticipantDeclaration {
    public NewParticipantDeclaration handle(Instant dateTime) throws JsonProcessingException {
        return handle(dateTime, null, null, 0, 1, 1, 1, null);
    }

    public NewParticipantDeclaration handle(Instant dateTime, String participantKnownId, List<String> serviceUrls,
            int keyIndex, int assertionKeyCount, int walletKeyCount, int keyAgreementKeyCount, List<Service> services)
            throws JsonProcessingException {
        String creationDate = TimeFormat.format(dateTime);
        Declarations declarations = new Declarations();
        declarations.setType("Declaration");
        declarations.setDeclaration(new ArrayList<>());

        String participantId = participantKnownId;
        Map<String, Object> methodMap = new HashMap<>();
        if (participantId == null) {
            // The first key we generate will provide the permanent participant identifier.
            if (assertionKeyCount > 0) {
                // If there is an assertion key request, we proceed with it.
                participantId = VerificationMethodUtils.idKeyPairFromAssertionKey(creationDate, keyIndex, methodMap);
                // Reduce count.
                assertionKeyCount -= 1;
            } else if (walletKeyCount > 0) {
                participantId = VerificationMethodUtils.idKeyPairFromWalletKey(creationDate, keyIndex, methodMap);
                walletKeyCount -= 1;
            } else {
                throw new IllegalArgumentException(
                        "Neither assertion not wallet key requested. Not signature key to generate identifier.");
            }
            // inccement key index right after generation
            keyIndex += 1;
        }

        ParticipantDeclaration participantDeclaration = services == null ? new ParticipantDeclaration()
                : new PerformanceDeclaration(services);
        declarations.getDeclaration().add(participantDeclaration);
        participantDeclaration.setId(participantId);
        participantDeclaration.setController(Arrays.asList(participantId));
        participantDeclaration.setCreated(creationDate);
        participantDeclaration.setVerificationMethod(new ArrayList<>());

        if (!methodMap.isEmpty()) {
            VerificationMethodData vm = (VerificationMethodData) methodMap.values().iterator().next();
            participantDeclaration.getVerificationMethod().add(vm.getVerificationMethod());
            participantDeclaration.getAssertionMethod().add(vm.getAssertionMethod());
        }

        // generate assertiion keys.
        keyIndex = VerificationMethodUtils.generateAssertionKeys(assertionKeyCount, participantId, creationDate,
                keyIndex, participantDeclaration, methodMap);

        // Bitcoin addresses
        keyIndex = VerificationMethodUtils.generateWalletKeys(walletKeyCount, participantId, creationDate, keyIndex,
                participantDeclaration, methodMap);

        // Key agreementn keys
        keyIndex = VerificationMethodUtils.generateKeyAgreementKeys(keyAgreementKeyCount, participantId, creationDate,
                keyIndex, participantDeclaration, methodMap);

        String recordString = JSON.encode(declarations);
        List<VerificationMethod> verificationMethodList = participantDeclaration.getVerificationMethod();
        for (int i = 0; i < verificationMethodList.size(); i++) {
            VerificationMethod vm = verificationMethodList.get(i);
            if (vm.getType() == CryptoService.ed25519KeyService.getType()) {
                recordString = ed25519Proof(participantId, recordString,
                        (VerificationMethodData) methodMap.get(vm.getId()), creationDate);
            } else if (vm.getType() == CryptoService.secp256k1KeyService.getType()) {
                recordString = secp256k1Proof(participantId, recordString,
                        (VerificationMethodData) methodMap.get(vm.getId()), creationDate);
            } else {
                throw new IllegalStateException("Unknown key type");
            }
        }

        return NewParticipantDeclaration.builder().record(recordString).id(participantId).verificationMethod(methodMap)
                .build();
    }

    private String ed25519Proof(String participantId, String record, VerificationMethodData vm, String creationDate) {
        Proof ed25519Proof = new Proof();
        ed25519Proof.setIssuer(participantId);
        ed25519Proof.setProofPurpose(ProofPurpose.PoP.name());
        ed25519Proof.setVerificationMethod(vm.getVerificationMethod().getId());
        ed25519Proof.setCreated(creationDate);
        ed25519Proof.setNonce(UUID.randomUUID().toString());
        return CryptoService.ed25519SignatureService.signDeclaration(record, ed25519Proof, vm.getKeyPair())
                .getSignedRecord();
    }

    private String secp256k1Proof(String participantId, String record, VerificationMethodData vm, String creationDate) {
        Proof secp256k1Proof = new Proof();
        secp256k1Proof.setIssuer(participantId);
        secp256k1Proof.setProofPurpose(ProofPurpose.PoP.name());
        secp256k1Proof.setVerificationMethod(vm.getVerificationMethod().getId());
        secp256k1Proof.setCreated(creationDate);
        secp256k1Proof.setNonce(UUID.randomUUID().toString());
        return CryptoService.secp256k1SignatureService.signDeclaration(record, secp256k1Proof, vm.getKeyPair())
                .getSignedRecord();

    }

}