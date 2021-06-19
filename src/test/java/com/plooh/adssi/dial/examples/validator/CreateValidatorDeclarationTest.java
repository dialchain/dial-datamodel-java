package com.plooh.adssi.dial.examples.validator;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.Instant;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.examples.participant.CreateParticipantDeclaration;
import com.plooh.adssi.dial.examples.participant.NewParticipantDeclaration;
import com.plooh.adssi.dial.parser.ParticipantDeclarationMapped;

import org.junit.jupiter.api.Test;

public class CreateValidatorDeclarationTest {
    @Test
    void testHandle() throws JsonProcessingException, JOSEException, NoSuchAlgorithmException, NoSuchProviderException {
        CreateParticipantDeclaration createParticipantDeclaration = new CreateParticipantDeclaration();
        NewParticipantDeclaration participant0 = createParticipantDeclaration.handle(Instant.now());
        NewParticipantDeclaration participant1 = createParticipantDeclaration.handle(Instant.now());
        NewParticipantDeclaration participant2 = createParticipantDeclaration.handle(Instant.now());

        CreateValidatorDeclaration createValidatorDeclaration = new CreateValidatorDeclaration();
        String dialRecordString = createValidatorDeclaration.handle(Instant.now(),
                Arrays.asList(getParticipantDeclaration(participant0.getRecord()),
                        getParticipantDeclaration(participant1.getRecord()),
                        getParticipantDeclaration(participant2.getRecord())));

        SignValidatorDeclaration signValidatorDeclaration = new SignValidatorDeclaration();
        dialRecordString = signValidatorDeclaration.handle(Instant.now(), dialRecordString, participant0);
        dialRecordString = signValidatorDeclaration.handle(Instant.now(), dialRecordString, participant1);
        dialRecordString = signValidatorDeclaration.handle(Instant.now(), dialRecordString, participant2);

        assertNotNull(dialRecordString);
    }

    ParticipantDeclaration getParticipantDeclaration(String recordString) {
        ParticipantDeclarationMapped doc = new ParticipantDeclarationMapped(recordString);
        return doc.declarations().get(0);
    }
}
