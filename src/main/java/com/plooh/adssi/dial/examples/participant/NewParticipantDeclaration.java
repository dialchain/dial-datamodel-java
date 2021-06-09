package com.plooh.adssi.dial.examples.participant;

import com.nimbusds.jose.jwk.OctetKeyPair;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewParticipantDeclaration {
    private String record;
    private OctetKeyPair keyPair;
}