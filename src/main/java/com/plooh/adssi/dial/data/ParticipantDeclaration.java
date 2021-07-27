package com.plooh.adssi.dial.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParticipantDeclaration extends DIDeclaration {
    public static final String TYPE = "Participant";

    private List<SignatureAssertionMethod> assertionMethod = new ArrayList<>();

    public ParticipantDeclaration(String type) {
        super(type == null ? TYPE : type);
    }
}