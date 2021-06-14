package com.plooh.adssi.dial.examples.publication;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.plooh.adssi.dial.crypto.CryptoService;
import com.plooh.adssi.dial.crypto.JcsBase64EcSignature2021Service;
import com.plooh.adssi.dial.data.OrganizationDeclaration;
import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.data.SignatureAssertionMethod;
import com.plooh.adssi.dial.data.VoteAssertionMethod;
import com.plooh.adssi.dial.examples.participant.NewParticipantDeclaration;
import com.plooh.adssi.dial.examples.participant.VerificationMethodData;
import com.plooh.adssi.dial.examples.validator.ValidatorMemberParticipant;
import com.plooh.adssi.dial.parser.OrganizationDeclarationMapped;
import com.plooh.adssi.dial.parser.ParticipantDeclarationMapped;
import com.plooh.adssi.dial.parser.SignedDocumentMapped;
import com.plooh.adssi.dial.parser.TimeFormat;

public class SignPublications {

    public String handle(Instant dateTime, String publicationString, NewParticipantDeclaration participant,
            String orgRecordString) {
        return handleInternal(dateTime, publicationString, participant, orgRecordString);
    }

    private String handleInternal(Instant dateTime, String publicationString, NewParticipantDeclaration participant,
            String orgRecordString) {
        String creationDate = TimeFormat.DTF.format(dateTime);
        OrganizationDeclarationMapped orgRecord = new OrganizationDeclarationMapped(orgRecordString);
        OrganizationDeclaration orgDeclaration = orgRecord.declarations().get(0);

        ParticipantDeclarationMapped participantRecord = new ParticipantDeclarationMapped(participant.getRecord());
        VoteAssertionMethod voteAssertionMethod = orgDeclaration.getAssertionMethod().get(0);
        List<ParticipantDeclaration> participantdeclarations = participantRecord.declarations();
        ValidatorMemberParticipant memberParticipant = ValidatorMemberParticipant
                .findMember(voteAssertionMethod.getMember(), participantdeclarations);

        SignatureAssertionMethod signatureAssertionmethod = memberParticipant.getParticipant().getAssertionMethod()
                .get(0);

        List<String> assertionMethods = Arrays.asList(voteAssertionMethod.getId(), signatureAssertionmethod.getId(),
                signatureAssertionmethod.getVerificationMethod());
        VerificationMethodData verificationMethodData = participant.getVerificationMethod()
                .get(signatureAssertionmethod.getVerificationMethod());

        SignedDocumentMapped sdm = new SignedDocumentMapped(publicationString);

        Proof proof = new Proof();
        proof.setDocument(sdm.id());
        proof.setIssuer(memberParticipant.getParticipant().getId());
        proof.setAssertionMethod(assertionMethods);
        proof.setCreated(creationDate);
        proof.setNonce(UUID.randomUUID().toString());

        JcsBase64EcSignature2021Service signatureService = CryptoService
                .findSignatureServiceForKey(verificationMethodData.getVerificationMethod().getType());
        return signatureService.sign(publicationString, verificationMethodData.getKeyPair(), proof);
    }
}