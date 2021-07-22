package com.plooh.adssi.dial.crypto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.plooh.adssi.dial.ReadFileUtils;
import com.plooh.adssi.dial.data.OctetKeyPair;
import com.plooh.adssi.dial.json.JSON;

import org.junit.jupiter.api.Test;

public class X25519JweServiceTest {
    @Test
    void testJweEncryptDecrypt() throws IOException {
        String keypairs = ReadFileUtils.readString(
                "./src/test/resources/dart-test-data/z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT-keypairs.json");
        JsonNode node = JSON.MAPPER.readTree(keypairs);
        OctetKeyPair octetKeyPair = JSON.MAPPER.convertValue(node.get("x25519"), OctetKeyPair.class);
        String recordJson = ReadFileUtils.readString(
                "./src/test/resources/dart-test-data/z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT-did.json");
        String jweEncrypted = CryptoService.x25519JweService.jweEncrypt(recordJson.getBytes(StandardCharsets.UTF_8),
                octetKeyPair.getPublicKey());
        CryptoService.x25519JweService.jweDecrypt(jweEncrypted, keySource);
    }

}
