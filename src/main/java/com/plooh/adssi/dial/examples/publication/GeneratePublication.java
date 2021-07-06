package com.plooh.adssi.dial.examples.publication;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plooh.adssi.dial.cid.CidUtils;
import com.plooh.adssi.dial.data.Publication;
import com.plooh.adssi.dial.json.JSON;

public class GeneratePublication {

    public String handle(Instant dateTime, String recordString, String orgRecordString) {
        try {
            return handleInternal(dateTime, recordString, orgRecordString);
        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String handleInternal(Instant dateTime, String recordString, String orgRecordString)
            throws JsonProcessingException, NoSuchAlgorithmException {
        Publication publication = new Publication();
        publication.setType("Publication");
        String jcsCidB58 = CidUtils.jcsCidB58(recordString);
        publication.setCid(Arrays.asList(jcsCidB58));

        return JSON.MAPPER.writeValueAsString(publication);
    }
}