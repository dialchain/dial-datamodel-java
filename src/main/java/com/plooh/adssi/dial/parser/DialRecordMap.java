package com.plooh.adssi.dial.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.plooh.adssi.dial.data.Declaration;
import com.plooh.adssi.dial.data.Declarations;
import com.plooh.adssi.dial.data.DialRecord;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.data.Twindow;
import com.plooh.adssi.dial.json.JSON;

public class DialRecordMap {
    private static TypeReference<List<Proof>> proofList = new TypeReference<List<Proof>>() {
    };
    private static DeclarationFactories declarationFactories = DeclarationFactories.getInstance();

    private static final String DECLARATION_KEY = "declaration";
    private static final String PROOF_KEY = "proof";
    private static final String PUBLICCATION_KEY = "publication";
    private static final String TWINDOW_KEY = "twindow";

    private static final String ID_KEY = "id";
    private static final String ENTRIES_KEY = "entries";

    private static final String TYPE_KEY = "type";

    private JsonNode map;

    public DialRecordMap(JsonNode map) {
        this.map = map;
    }

    public static DialRecordMap parse(String dialRecordString) {
        try {
            return new DialRecordMap(JSON.MAPPER.readTree(dialRecordString));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Proof> proof() {
        return JSON.MAPPER.convertValue(map.get(PROOF_KEY), proofList);
    }

    public List<Proof> publication() {
        return JSON.MAPPER.convertValue(map.get(PUBLICCATION_KEY), proofList);
    }

    public String declarationString() {
        JsonNode declMap = map.get(DECLARATION_KEY);
        try {
            return JSON.MAPPER.writeValueAsString(declMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String id() {
        JsonNode declMap = map.get(DECLARATION_KEY);
        return declMap.get(ID_KEY).asText();
    }

    public List<Declaration> entries() {
        JsonNode declMap = map.get(DECLARATION_KEY);
        List<Declaration> result = new ArrayList<>();
        Iterator<JsonNode> elements = declMap.get(ENTRIES_KEY).elements();
        while (elements.hasNext()) {
            JsonNode declNode = elements.next();
            String typeName = declNode.get(TYPE_KEY).asText();
            TypeReference<? extends Declaration> typeRef = declarationFactories.getTypeRef(typeName);
            if (typeRef == null) {
                throw new IllegalStateException("No type reference found for " + typeName);
            }
            Declaration decl = JSON.MAPPER.convertValue(declNode, typeRef);
            result.add(decl);
        }
        return result;
    }

    public Twindow twindow() {
        return JSON.MAPPER.convertValue(map.get(TWINDOW_KEY), Twindow.class);
    }

    DialRecord record;

    public DialRecord mutable() {
        if (record != null)
            return record;

        record = new DialRecord();

        Declarations declarations = new Declarations();
        declarations.setId(id());
        declarations.setEntries(entries());

        record.setDeclaration(declarations);
        record.setProof(proof());
        record.setPublication(publication());
        record.setTwindow((twindow()));

        return record;
    }

}
