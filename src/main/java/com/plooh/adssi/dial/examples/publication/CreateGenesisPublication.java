package com.plooh.adssi.dial.examples.publication;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.plooh.adssi.dial.examples.participant.NewParticipantDeclaration;

public class CreateGenesisPublication {
    /**
     * Validate and publishes the genesis files of this validator.
     * 
     * @param dateTime  time of publication
     * @param orgRecord the validator record
     * @param records   three node records
     * @param signers   the validator nodes signing
     * @return
     */
    public List<String> handle(Instant dateTime, String orgRecord, List<String> records,
            List<NewParticipantDeclaration> signers) {
        GeneratePublication generatePublication = new GeneratePublication();
        SignPublications signPublications = new SignPublications();

        List<String> publications = new ArrayList<>();
        records.forEach(recordString -> {
            publications.add(generatePublication.handle(dateTime, recordString));
        });

        List<String> result = new ArrayList<>();
        publications.forEach(ps -> {
            String publicationString = ps;
            for (int i = 0; i < signers.size(); i++) {
                NewParticipantDeclaration signer = signers.get(i);
                publicationString = signPublications.handle(dateTime, publicationString, signer, orgRecord);
            }
            result.add(publicationString);
        });

        return result;
    }
}