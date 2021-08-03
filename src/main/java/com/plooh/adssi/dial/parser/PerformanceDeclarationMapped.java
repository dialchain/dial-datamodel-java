package com.plooh.adssi.dial.parser;

import java.util.List;

import com.jayway.jsonpath.TypeRef;
import com.plooh.adssi.dial.data.PerformanceDeclaration;

public class PerformanceDeclarationMapped extends SignedDocumentMapped {
    private static TypeRef<List<PerformanceDeclaration>> declarationTypes = new TypeRef<List<PerformanceDeclaration>>() {
    };

    public PerformanceDeclarationMapped(String recordString) {
        super(recordString);
    }

    public List<PerformanceDeclaration> declarations() {
        return doc.read("$.declaration", declarationTypes);
    }
}