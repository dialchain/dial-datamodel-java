package com.plooh.adssi.dial.examples.publication;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.plooh.adssi.dial.cid.CidUtils;
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
    public Map<String, String> handle(Instant dateTime, String orgRecord, List<String> records,
            List<NewParticipantDeclaration> signers) {
        GeneratePublication generatePublication = new GeneratePublication();
        SignPublications signPublications = new SignPublications();

        Map<String, String> result = new HashMap<>();

        List<String> publications = new ArrayList<>();
        records.forEach(recordString -> {
            result.put(CidUtils.jcsCidB58(recordString), recordString);
            publications.add(generatePublication.handle(dateTime, recordString, orgRecord));
        });

        publications.forEach(ps -> {
            String publicationString = ps;
            for (int i = 0; i < signers.size(); i++) {
                NewParticipantDeclaration signer = signers.get(i);
                publicationString = signPublications.handle(dateTime, publicationString, signer, orgRecord);
            }
            result.put(CidUtils.jcsCidB58(publicationString), publicationString);
        });

        return result;
    }
}