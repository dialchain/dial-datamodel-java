package com.plooh.adssi.dial.examples.participant;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class NewParticipantDeclaration {
    private String record;
    private Map<String, VerificationMethodData> verificationMethod = new HashMap<>();
}