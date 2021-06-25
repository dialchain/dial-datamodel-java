package com.plooh.adssi.dial.data;

import java.util.List;

import lombok.Data;

@Data
public class ParticipantDeclaration extends DIDeclaration {
    public static final String TYPE = "Participant";

    private List<SignatureAssertionMethod> assertionMethod;

    public ParticipantDeclaration() {
        super(TYPE);
    }
}