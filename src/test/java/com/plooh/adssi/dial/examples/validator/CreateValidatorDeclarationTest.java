package com.plooh.adssi.dial.examples.validator;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plooh.adssi.dial.examples.participant.CreateParticipantDeclaration;
import com.plooh.adssi.dial.examples.participant.NewParticipantDeclaration;

import org.junit.jupiter.api.Test;

public class CreateValidatorDeclarationTest {
    @Test
    void testHandle() throws JsonProcessingException {
        CreateParticipantDeclaration createParticipantDeclaration = new CreateParticipantDeclaration();
        NewParticipantDeclaration participant0 = createParticipantDeclaration.handle(Instant.now());
        NewParticipantDeclaration participant1 = createParticipantDeclaration.handle(Instant.now());
        NewParticipantDeclaration participant2 = createParticipantDeclaration.handle(Instant.now());

        CreateValidatorDeclaration createValidatorDeclaration = new CreateValidatorDeclaration();
        String dialRecordString = createValidatorDeclaration.handle(Instant.now(),
                Arrays.asList(participant0.getParticipantDeclaration(), participant1.getParticipantDeclaration(),
                        participant2.getParticipantDeclaration()));

        SignValidatorDeclaration signValidatorDeclaration = new SignValidatorDeclaration();
        dialRecordString = signValidatorDeclaration.handle(Instant.now(), dialRecordString, participant0);
        dialRecordString = signValidatorDeclaration.handle(Instant.now(), dialRecordString, participant1);
        dialRecordString = signValidatorDeclaration.handle(Instant.now(), dialRecordString, participant2);

        assertNotNull(dialRecordString);
    }
}
