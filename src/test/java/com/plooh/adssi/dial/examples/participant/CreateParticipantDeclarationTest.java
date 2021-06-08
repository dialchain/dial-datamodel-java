package com.plooh.adssi.dial.examples.participant;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import com.fasterxml.jackson.core.JsonProcessingException;

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
}
