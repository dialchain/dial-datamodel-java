package com.plooh.adssi.dial.examples.participant;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewParticipantDeclaration {
    private String id;
    private String record;
    private Map<String, Object> verificationMethod = new HashMap<>();
    private int keyIndex;
}