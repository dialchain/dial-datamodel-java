package com.plooh.adssi.dial.parser;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.nimbusds.jose.util.IOUtils;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.jcs.JCS;

import org.junit.jupiter.api.Test;

public class SignedDocumentMappedTest {
    @Test
    void testAddProof() throws IOException {
        String input = read("/signedDocumentMappedTest_input.json");
        String output = read("/signedDocumentMappedTest_output.json");
        SignedDocumentMapped sdm = new SignedDocumentMapped(input);

        Proof proof = new Proof();

        sdm.addProof(proof);
        String json = sdm.toJson();
        JCS.encode(json).equals(JCS.encode(output));
    }

    @Test
    void testDeleteProof() {
        List<Integer> array = new ArrayList<Integer>() {
            {
                add(0);
                add(1);
            }
        };
        DocumentContext add = JsonPath.parse(array).add("$", 2);
        String jsonString = add.jsonString();
        assertNotNull(jsonString);
    }

    private String read(String path) throws IOException {
        InputStream is = this.getClass().getResourceAsStream(path);
        return IOUtils.readInputStreamToString(is, StandardCharsets.UTF_8);
    }
}
