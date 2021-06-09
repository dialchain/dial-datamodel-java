package com.plooh.adssi.dial.parser;

import java.util.ArrayList;
import java.util.List;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.TypeRef;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.json.JsonPathUtils;

public class SignedDocumentMapped {

    private static final String ROOT_PATH = "$";
    private static final String PROOF_PATH = "$.proof";
    private static final String PROOF_KEY = "proof";
    private static TypeRef<List<Proof>> proofList = new TypeRef<List<Proof>>() {
    };
    protected DocumentContext doc;

    public SignedDocumentMapped(String recordString) {
        doc = JsonPathUtils.parse(recordString);
    }

    public List<Proof> proof() {
        try {
            return doc.read(PROOF_PATH, proofList);
        } catch (PathNotFoundException e) {
            return null;
        }
    }

    public String id() {
        return doc.read("$.id", String.class);
    }

    public String type() {
        return doc.read("$.type", String.class);
    }

    public SignedDocumentMapped deleteProof() {
        doc.delete(PROOF_PATH);
        return this;
    }

    public SignedDocumentMapped addProof(Proof proof) {
        List<Proof> list = proof();
        if (list == null) {
            doc = doc.put(ROOT_PATH, PROOF_KEY, new ArrayList<Proof>());
        }
        doc = doc.add(PROOF_PATH, proof);
        return this;
    }

    public String toJson() {
        return doc.jsonString();
    }
}
