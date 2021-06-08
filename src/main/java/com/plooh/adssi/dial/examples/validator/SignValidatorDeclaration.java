package com.plooh.adssi.dial.examples.validator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plooh.adssi.dial.crypto.JcsBase64Ed25519Signature2021Service;
import com.plooh.adssi.dial.data.Declaration;
import com.plooh.adssi.dial.data.DialRecord;
import com.plooh.adssi.dial.data.OrganizationDeclaration;
import com.plooh.adssi.dial.data.OrganizationMember;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.data.VoteAssertionMethod;
import com.plooh.adssi.dial.examples.participant.NewParticipantDeclaration;
import com.plooh.adssi.dial.json.JSON;
import com.plooh.adssi.dial.parser.DialRecordMap;
import com.plooh.adssi.dial.parser.TimeFormat;

public class SignValidatorDeclaration {

    public String handle(Instant dateTime, String dialRecordString, NewParticipantDeclaration participant)
            throws JsonProcessingException {
        String creationDate = TimeFormat.DTF.format(dateTime);
        DialRecordMap orgRecord = DialRecordMap.parse(dialRecordString);
        OrganizationDeclaration orgDeclaration = (OrganizationDeclaration) orgRecord.entries().get(0);
        VoteAssertionMethod voteAssertionMethod = orgDeclaration.getAssertionMethod().get(0);

        DialRecordMap participantRecord = DialRecordMap.parse(participant.getRecord());
        List<Declaration> participants = participantRecord.entries();

        List<OrganizationMember> members = voteAssertionMethod.getMember();

        ValidatorMemberParticipant memberParticipant = ValidatorMemberParticipant.findMember(members, participants);

        List<String> assertionMethods = Arrays.asList(voteAssertionMethod.getId(),
                memberParticipant.participant.getAssertionMethod().get(0).getId());

        Proof proof = new Proof();
        proof.setDeclaration(orgRecord.id());
        proof.setIssuer(memberParticipant.participant.getId());
        proof.setAssertionMethod(assertionMethods);
        proof.setCreated(creationDate);
        proof.setNonce(UUID.randomUUID().toString());
        HashMap<String, Object> headerParams = JSON.MAPPER.convertValue(proof,
                JcsBase64Ed25519Signature2021Service.typeRefMap);
        Map<String, Object> result = JcsBase64Ed25519Signature2021Service.sign(orgRecord.declarationString(),
                participant.getKeyPair(), headerParams);
        proof = JSON.MAPPER.convertValue(result, Proof.class);

        DialRecord mutableRecord = orgRecord.mutable();
        if (mutableRecord.getProof() == null) {
            mutableRecord.setProof(new ArrayList<>());
        }
        mutableRecord.getProof().add(proof);

        return JSON.MAPPER.writeValueAsString(mutableRecord);
    }
}