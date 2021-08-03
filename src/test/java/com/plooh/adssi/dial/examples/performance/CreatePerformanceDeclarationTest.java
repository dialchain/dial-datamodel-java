package com.plooh.adssi.dial.examples.performance;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plooh.adssi.dial.ReadFileUtils;
import com.plooh.adssi.dial.examples.participant.CreateParticipantDeclaration;
import com.plooh.adssi.dial.examples.participant.NewParticipantDeclaration;

import org.junit.jupiter.api.Test;

public class CreatePerformanceDeclarationTest {
    final List<String> serviceUrls = Arrays.asList("https://node0.first-dial.io/", "https://node23.all-cloud.net/",
            "https://www1.empire.us/");

    @Test
    void testHandle() throws JsonProcessingException {
        CreateParticipantDeclaration createParticipantDeclaration = new CreateParticipantDeclaration();
        NewParticipantDeclaration participant0 = createParticipantDeclaration.handle(Instant.now());
        CreatePerformanceDeclaration createPerformanceDeclaration = new CreatePerformanceDeclaration();
        NewParticipantDeclaration performanceDeclaration = createPerformanceDeclaration.handle(Instant.now(),
                participant0, serviceUrls);
        assertNotNull(performanceDeclaration);
    }

    @Test
    void testVerifyStaticDeclaration() throws IOException {
        String participantRecord = ReadFileUtils.readString(
                "./src/test/resources/java-test-data/zGrhSzwLzhxDHUfvgdCYvCA6TQTCePoumfLqJKBcXZ9qU-perf.json");
        VerifyPerformanceDeclaration verifyPerformanceDeclaration = new VerifyPerformanceDeclaration();

        boolean verified = verifyPerformanceDeclaration.handle(participantRecord);
        assertTrue(verified);
    }
}
