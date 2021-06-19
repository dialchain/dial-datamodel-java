package com.plooh.adssi.dial.examples.publication;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import com.plooh.adssi.dial.data.ParticipantDeclaration;
import com.plooh.adssi.dial.examples.participant.CreateParticipantDeclaration;
import com.plooh.adssi.dial.examples.participant.NewParticipantDeclaration;
import com.plooh.adssi.dial.examples.validator.CreateValidatorDeclaration;
import com.plooh.adssi.dial.examples.validator.SignValidatorDeclaration;
import com.plooh.adssi.dial.parser.ParticipantDeclarationMapped;

import org.junit.jupiter.api.Test;

public class CreateGenesisPublicationTest {
    @Test
    void testHandle() throws JsonProcessingException, JOSEException, NoSuchAlgorithmException, NoSuchProviderException {
        CreateParticipantDeclaration createParticipantDeclaration = new CreateParticipantDeclaration();
        NewParticipantDeclaration participant0 = createParticipantDeclaration.handle(Instant.now());
        NewParticipantDeclaration participant1 = createParticipantDeclaration.handle(Instant.now());
        NewParticipantDeclaration participant2 = createParticipantDeclaration.handle(Instant.now());

        CreateValidatorDeclaration createValidatorDeclaration = new CreateValidatorDeclaration();
        String validatorRecordString = createValidatorDeclaration.handle(Instant.now(),
                Arrays.asList(getParticipantDeclaration(participant0.getRecord()),
                        getParticipantDeclaration(participant1.getRecord()),
                        getParticipantDeclaration(participant2.getRecord())));

        SignValidatorDeclaration signValidatorDeclaration = new SignValidatorDeclaration();
        validatorRecordString = signValidatorDeclaration.handle(Instant.now(), validatorRecordString, participant0);
        validatorRecordString = signValidatorDeclaration.handle(Instant.now(), validatorRecordString, participant1);
        validatorRecordString = signValidatorDeclaration.handle(Instant.now(), validatorRecordString, participant2);

        CreateGenesisPublication createGenesisPublication = new CreateGenesisPublication();
        List<NewParticipantDeclaration> signers = Arrays.asList(participant1, participant2);
        List<String> records = Arrays.asList(participant0.getRecord(), participant1.getRecord(),
                participant2.getRecord(), validatorRecordString);
        List<String> genesisRecords = createGenesisPublication.handle(Instant.now(), validatorRecordString, records,
                signers);
        assertTrue(genesisRecords.size() == 4);
    }

    ParticipantDeclaration getParticipantDeclaration(String recordString) {
        ParticipantDeclarationMapped doc = new ParticipantDeclarationMapped(recordString);
        return doc.declarations().get(0);
    }
}
