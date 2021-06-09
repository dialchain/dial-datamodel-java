package com.plooh.adssi.dial.examples.validator;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.plooh.adssi.dial.crypto.JcsBase64Ed25519Signature2021Service;
import com.plooh.adssi.dial.data.OrganizationDeclaration;
import com.plooh.adssi.dial.data.OrganizationMember;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.data.VoteAssertionMethod;
import com.plooh.adssi.dial.examples.participant.NewParticipantDeclaration;
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

        List<OrganizationMember> members = voteAssertionMethod.getMember();

        ValidatorMemberParticipant memberParticipant = ValidatorMemberParticipant.findMember(members,
                participantRecord.declarations());

        List<String> assertionMethods = Arrays.asList(voteAssertionMethod.getId(),
                memberParticipant.getParticipant().getAssertionMethod().get(0).getId());

        Proof proof = new Proof();
        proof.setDocument(orgRecord.id());
        proof.setIssuer(memberParticipant.getParticipant().getId());
        proof.setAssertionMethod(assertionMethods);
        proof.setCreated(creationDate);
        proof.setNonce(UUID.randomUUID().toString());
        return JcsBase64Ed25519Signature2021Service.sign(dialRecordString, participant.getKeyPair(), proof);
    }
}