package com.plooh.adssi.dial.examples.validator;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.plooh.adssi.dial.crypto.CryptoService;
import com.plooh.adssi.dial.crypto.JcsBase64EcSignature2021Service;
import com.plooh.adssi.dial.data.OrganizationDeclaration;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.data.SignatureAssertionMethod;
import com.plooh.adssi.dial.data.VoteAssertionMethod;
import com.plooh.adssi.dial.examples.participant.NewParticipantDeclaration;
import com.plooh.adssi.dial.examples.participant.VerificationMethodData;
import com.plooh.adssi.dial.parser.OrganizationDeclarationMapped;
import com.plooh.adssi.dial.parser.ParticipantDeclarationMapped;
import com.plooh.adssi.dial.parser.TimeFormat;

public class SignValidatorDeclaration {

    public String handle(Instant dateTime, String dialRecordString, NewParticipantDeclaration participant) {
        String creationDate = TimeFormat.DTF.format(dateTime);
        OrganizationDeclarationMapped orgRecord = new OrganizationDeclarationMapped(dialRecordString);
        OrganizationDeclaration orgDeclaration = orgRecord.declarations().get(0);
        VoteAssertionMethod voteAssertionMethod = orgDeclaration.getAssertionMethod().get(0);

        ParticipantDeclarationMapped participantRecord = new ParticipantDeclarationMapped(participant.getRecord());

        ValidatorMemberParticipant memberParticipant = ValidatorMemberParticipant
                .findMember(voteAssertionMethod.getMember(), participantRecord.declarations());

        SignatureAssertionMethod signatureAssertionmethod = memberParticipant.getParticipant().getAssertionMethod()
                .get(0);
        List<String> assertionMethods = Arrays.asList(voteAssertionMethod.getId(), signatureAssertionmethod.getId());

        VerificationMethodData verificationMethodData = participant.getVerificationMethod()
                .get(signatureAssertionmethod.getVerificationMethod());

        Proof proof = new Proof();
        proof.setIssuer(memberParticipant.getParticipant().getId());
        proof.setAssertionMethod(assertionMethods);
        proof.setVerificationMethod(signatureAssertionmethod.getVerificationMethod());
        proof.setCreated(creationDate);
        proof.setNonce(UUID.randomUUID().toString());
        JcsBase64EcSignature2021Service signatureService = CryptoService
                .findSignatureServiceForKey(verificationMethodData.getVerificationMethod().getType());
        return signatureService.sign(dialRecordString, verificationMethodData.getKeyPair(), proof);
    }
}