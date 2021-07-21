package com.plooh.adssi.dial.rules;

import com.plooh.adssi.dial.examples.participant.CreateParticipantDeclaration;
import com.plooh.adssi.dial.examples.participant.NewParticipantDeclaration;
import java.io.IOException;
import java.time.Instant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParticipantDeclarationRulesTest {

    ParticipantDeclarationRules rules = new ParticipantDeclarationRules();

    @Test
    void should_checkDIAL_001_001_Success() throws IOException {
        // Sign
        CreateParticipantDeclaration createParticipantDeclaration = new CreateParticipantDeclaration();
        final NewParticipantDeclaration newParticipantDeclaration = createParticipantDeclaration.handle(Instant.now());
        Assertions.assertThat(newParticipantDeclaration).isNotNull();

        // Verify
        var validationResults = rules.checkDIAL_001_001(newParticipantDeclaration.getRecord());
        Assertions.assertThat(validationResults).isNotNull();
    };

}
