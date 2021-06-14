package com.plooh.adssi.dial.parser;

public class PublicationMapped extends SignedDocumentMapped {
    // private static TypeRef<List<ParticipantDeclaration>> declarationTypes = new
    // TypeRef<List<ParticipantDeclaration>>() {
    // };

    public PublicationMapped(String recordString) {
        super(recordString);
    }

    // public List<Decl> declarations() {
    // return doc.read("$.declaration", declarationTypes);
    // }
}