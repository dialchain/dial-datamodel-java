package com.plooh.adssi.dial.parser;

import java.util.List;

import com.jayway.jsonpath.TypeRef;
import com.plooh.adssi.dial.data.ParticipantDeclaration;

public class ParticipantDeclarationMapped extends SignedDocumentMapped {
    private static TypeRef<List<ParticipantDeclaration>> declarationTypes = new TypeRef<List<ParticipantDeclaration>>() {
    };

    public ParticipantDeclarationMapped(String recordString) {
        super(recordString);
    }

    public List<ParticipantDeclaration> declarations() {
        return doc.read("$.declaration", declarationTypes);
    }
}