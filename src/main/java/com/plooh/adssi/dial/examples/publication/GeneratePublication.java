package com.plooh.adssi.dial.examples.publication;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.plooh.adssi.dial.data.AddressType;
import com.plooh.adssi.dial.data.Publication;
import com.plooh.adssi.dial.data.Twindow;
import com.plooh.adssi.dial.json.JSON;
import com.plooh.adssi.dial.twindow.TwindowUtils;

public class GeneratePublication {

    public String handle(Instant dateTime, String recordString) {
        try {
            return handleInternal(dateTime, recordString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String handleInternal(Instant dateTime, String recordString)
            throws JsonMappingException, JsonProcessingException {
        Publication publication = new Publication();
        JsonNode document = JSON.MAPPER.readTree(recordString);
        publication.setDocument(document);
        publication.setId(AddressType.uuid.normalize(UUID.randomUUID().toString()));
        publication.setType("Publication");

        Twindow twindow = TwindowUtils.twindow(dateTime);
        publication.setTwindow(twindow);

        return JSON.MAPPER.writeValueAsString(publication);
    }
}