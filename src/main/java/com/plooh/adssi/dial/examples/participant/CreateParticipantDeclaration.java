package com.plooh.adssi.dial.examples.participant;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import com.plooh.adssi.dial.crypto.BitcoinAddress;
import com.plooh.adssi.dial.crypto.CryptoService;
import com.plooh.adssi.dial.data.Declarations;
import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.data.ProofPurpose;
import com.plooh.adssi.dial.data.SignatureAssertionMethod;
import com.plooh.adssi.dial.data.TreasuryAccount;
import com.plooh.adssi.dial.data.TreasuryAccountControler;
import com.plooh.adssi.dial.examples.utils.VerificationMethodUtils;
import com.plooh.adssi.dial.json.JSON;
import com.plooh.adssi.dial.parser.TimeFormat;

import org.bitcoinj.params.MainNetParams;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class CreateParticipantDeclaration {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public NewParticipantDeclaration handle(Instant dateTime)
            throws JsonProcessingException, JOSEException, NoSuchAlgorithmException, NoSuchProviderException {
        String creationDate = TimeFormat.format(dateTime);
        Declarations declarations = new Declarations();
        declarations.setType("Declaration");
        declarations.setDeclaration(new ArrayList<>());

        ParticipantDeclaration participantDeclaration = new ParticipantDeclaration();
        declarations.getDeclaration().add(participantDeclaration);
        participantDeclaration.setCreated(creationDate);
        // String participantId =
        // AddressType.uuid.normalize(UUID.randomUUID().toString());

        participantDeclaration.setVerificationMethod(new ArrayList<>());
        VerificationMethodData verif_ed25519 = VerificationMethodUtils.verificationMethodEd25519(null, creationDate, 0);
        participantDeclaration.getVerificationMethod().add(verif_ed25519.getVerificationMethod());
        String participantId = verif_ed25519.getVerificationMethod().getPublicKeyMultibase();
        participantDeclaration.setId(participantId);
        participantDeclaration.setController(Arrays.asList(participantId));

        VerificationMethodData verif_secp2561 = VerificationMethodUtils.verificationMethodSecp256k1(participantId,
                creationDate, 1);
        participantDeclaration.getVerificationMethod().add(verif_secp2561.getVerificationMethod());

        SignatureAssertionMethod signatureAssertionMethod = new SignatureAssertionMethod(
                participantId + "#" + creationDate + "#am-0");
        signatureAssertionMethod.setVerificationMethod(verif_ed25519.getId());
        participantDeclaration.setAssertionMethod(Arrays.asList(signatureAssertionMethod));

        TreasuryAccount treasuryAccount = new TreasuryAccount();
        String encodedPk = BitcoinAddress.p2wpkhAddress(MainNetParams.get(),
                verif_secp2561.getKeyPair().toECKey().toECPublicKey());

        treasuryAccount.setAddress(encodedPk);
        treasuryAccount.setNetwork(MainNetParams.get().getId());
        TreasuryAccountControler control = new TreasuryAccountControler();
        control.setQuorum(1);
        control.setVerificationMethod(Arrays.asList(verif_secp2561.getVerificationMethod().getId()));
        treasuryAccount.setControl(control);
        participantDeclaration.setAccount(Arrays.asList(treasuryAccount));

        Proof ed25519Proof = new Proof();
        ed25519Proof.setIssuer(participantId);
        ed25519Proof.setProofPurpose(ProofPurpose.PoP.name());
        ed25519Proof.setVerificationMethod(verif_ed25519.getVerificationMethod().getId());
        ed25519Proof.setCreated(creationDate);
        ed25519Proof.setNonce(UUID.randomUUID().toString());
        String signedRecord = CryptoService.ed25519SignatureService.sign(JSON.MAPPER.writeValueAsString(declarations),
                verif_ed25519.getKeyPair(), ed25519Proof);

        Proof secp256k1Proof = new Proof();
        secp256k1Proof.setIssuer(participantId);
        secp256k1Proof.setProofPurpose(ProofPurpose.PoP.name());
        secp256k1Proof.setVerificationMethod(verif_secp2561.getVerificationMethod().getId());
        secp256k1Proof.setCreated(creationDate);
        secp256k1Proof.setNonce(UUID.randomUUID().toString());
        signedRecord = CryptoService.secp256k1SignatureService.sign(signedRecord, verif_secp2561.getKeyPair(),
                secp256k1Proof);

        NewParticipantDeclaration result = new NewParticipantDeclaration();
        result.setRecord(signedRecord);
        result.getVerificationMethod().put(verif_ed25519.getId(), verif_ed25519);
        result.getVerificationMethod().put(verif_secp2561.getId(), verif_secp2561);

        return result;
    }
}