package com.plooh.adssi.dial.parser;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.plooh.adssi.dial.data.Declaration;
import com.plooh.adssi.dial.data.OrganizationDeclaration;
import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.data.ReferencedDeclaration;

public class DeclarationFactories {
    private static Map<String, TypeReference<? extends Declaration>> typeMap = new HashMap<>();
    private static DeclarationFactories instance = new DeclarationFactories();

    public static DeclarationFactories getInstance() {
        return instance;
    }

    private DeclarationFactories() {
        typeMap.put(ParticipantDeclaration.TYPE, new TypeReference<ParticipantDeclaration>() {
        });
        typeMap.put(OrganizationDeclaration.TYPE, new TypeReference<OrganizationDeclaration>() {
        });
        typeMap.put(ReferencedDeclaration.TYPE, new TypeReference<ReferencedDeclaration>() {
        });
    }

    public TypeReference<? extends Declaration> getTypeRef(String typeName) {
        return typeMap.get(typeName);
    }

    public void addTypeRef(String typeName, TypeReference<? extends Declaration> typeRef) {
        if (typeMap.containsKey(typeName))
            throw new IllegalStateException("Type with name " + typeName + " already exists");
    }
}