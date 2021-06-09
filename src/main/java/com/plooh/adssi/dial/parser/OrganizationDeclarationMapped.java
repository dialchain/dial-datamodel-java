package com.plooh.adssi.dial.parser;

import java.util.List;

import com.jayway.jsonpath.TypeRef;
import com.plooh.adssi.dial.data.OrganizationDeclaration;

public class OrganizationDeclarationMapped extends SignedDocumentMapped {
    private static TypeRef<List<OrganizationDeclaration>> declarationTypes = new TypeRef<List<OrganizationDeclaration>>() {
    };

    public OrganizationDeclarationMapped(String recordString) {
        super(recordString);
    }

    public List<OrganizationDeclaration> declarations() {
        return doc.read("$.declaration", declarationTypes);
    }
}