package com.plooh.adssi.dial.examples.participant;

import com.nimbusds.jose.jwk.OctetKeyPair;
import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.parser.DialRecordMap;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewParticipantDeclaration {
    private String record;
    private OctetKeyPair keyPair;

    public ParticipantDeclaration getParticipantDeclaration() {
        DialRecordMap participantRecord = DialRecordMap.parse(record);
        return (ParticipantDeclaration) participantRecord.entries().get(0);
    }
}