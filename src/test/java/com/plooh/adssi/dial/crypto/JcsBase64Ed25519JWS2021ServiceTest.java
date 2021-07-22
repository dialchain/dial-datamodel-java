package com.plooh.adssi.dial.crypto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.plooh.adssi.dial.ReadFileUtils;
import com.plooh.adssi.dial.data.OctetKeyPair;
import com.plooh.adssi.dial.json.JSON;

import org.junit.jupiter.api.Test;

public class JcsBase64Ed25519JWS2021ServiceTest {
    @Test
    void testSignJWS() throws IOException {
        String keypairs = ReadFileUtils.readString(
                "./src/test/resources/dart-test-data/z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT-keypairs.json");
        String recordJson = ReadFileUtils.readString(
                "./src/test/resources/dart-test-data/z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT-half-signed.json");
        JsonNode node = JSON.MAPPER.readTree(keypairs);
        OctetKeyPair octetKeyPair = JSON.MAPPER.convertValue(node.get("ed25519"), OctetKeyPair.class);

        String signedJWS = CryptoService.ed25519JwsService.signJWS(recordJson, octetKeyPair, false);

        boolean verifyAttached = CryptoService.ed25519JwsService.verifyAttached(signedJWS, octetKeyPair.getPublicKey());
        assertTrue(verifyAttached);
    }

    @Test
    void testVerifyDartJWS() throws IOException {
        String keypairs = ReadFileUtils.readString(
                "./src/test/resources/dart-test-data/z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT-keypairs.json");
        JsonNode node = JSON.MAPPER.readTree(keypairs);
        OctetKeyPair octetKeyPair = JSON.MAPPER.convertValue(node.get("ed25519"), OctetKeyPair.class);

        String signedJWS = ReadFileUtils
                .readString("./src/test/resources/dart-test-data/z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT.jws");

        boolean verifyAttached = CryptoService.ed25519JwsService.verifyAttached(signedJWS, octetKeyPair.getPublicKey());
        assertTrue(verifyAttached);
    }
}
