package com.plooh.adssi.dial.examples.validator;

import java.util.List;

import com.plooh.adssi.dial.data.OrganizationMember;
import com.plooh.adssi.dial.data.ParticipantDeclaration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidatorMemberParticipant {
    OrganizationMember member;
    ParticipantDeclaration participant;

    public static ValidatorMemberParticipant findMember(List<OrganizationMember> members,
            List<ParticipantDeclaration> participants) {
        for (int i = 0; i < members.size(); i++) {
            OrganizationMember organizationMember = members.get(i);
            for (int j = 0; j < participants.size(); j++) {
                ParticipantDeclaration participantDeclaration = participants.get(j);
                if (organizationMember.getId().equals(participantDeclaration.getId())) {
                    return new ValidatorMemberParticipant(organizationMember, participantDeclaration);
                }
            }
        }
        throw new IllegalStateException("Missing member  entry for given participant.");
    }
}