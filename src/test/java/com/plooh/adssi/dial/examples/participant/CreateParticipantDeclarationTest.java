package com.plooh.adssi.dial.examples.participant;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.Instant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plooh.adssi.dial.ReadFileUtils;

import org.junit.jupiter.api.Test;

public class CreateParticipantDeclarationTest {
    @Test
    void testHandle() throws JsonProcessingException {
        CreateParticipantDeclaration createParticipantDeclaration = new CreateParticipantDeclaration();
        VerifyParticipantDeclaration verifyParticipantDeclaration = new VerifyParticipantDeclaration();

        NewParticipantDeclaration newDecl = createParticipantDeclaration.handle(Instant.now());
        boolean verified = verifyParticipantDeclaration.handle(newDecl.getRecord());
        assertTrue(verified);
    }

    @Test
    void testVerifyStaticDeclaration() throws IOException {
        String participantRecord = ReadFileUtils.readString(
                "./src/test/resources/java-test-data/z3F3P8rTpf8vP2zGv6BWqtrCNrW4WG3rQzYXBAv22ZXtm-did.json");
        VerifyParticipantDeclaration verifyParticipantDeclaration = new VerifyParticipantDeclaration();

        boolean verified = verifyParticipantDeclaration.handle(participantRecord);
        assertTrue(verified);
    }

    @Test
    void testVerifyStaticDeclarationFromDart() throws IOException {
        String participantRecord = ReadFileUtils.readString(
                "./src/test/resources/dart-test-data/z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT-did.json");
        VerifyParticipantDeclaration verifyParticipantDeclaration = new VerifyParticipantDeclaration();

        boolean verified = verifyParticipantDeclaration.handle(participantRecord);
        assertTrue(verified);
    }
}
