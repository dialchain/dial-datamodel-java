package com.plooh.adssi.dial.crypto;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.plooh.adssi.dial.ReadFileUtils;
import com.plooh.adssi.dial.data.OctetKeyPair;
import com.plooh.adssi.dial.encode.Base64URL;
import com.plooh.adssi.dial.json.JSON;
import com.plooh.adssi.dial.key.SingleX25519KeySource;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class X25519JweServiceTest {
    @Test
    void testJweEncryptDecrypt() throws IOException {
        // read key
        final OctetKeyPair octetKeyPair = readKey(
                "./src/test/resources/dart-test-data/z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT-keypairs.json");
        // load plain
        String recordJson = ReadFileUtils.readString(
                "./src/test/resources/dart-test-data/z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT-did.json");
        // encrypt plain
        byte[] recordBytes = Base64URL.jcs_utf8_base64url(recordJson).getBytes(StandardCharsets.US_ASCII);
        String jweEncrypted = CryptoService.x25519JweService.jweEncrypt(recordBytes, octetKeyPair.getPublicKey());
        // decrypt cypher
        byte[] jweDecrypted = CryptoService.x25519JweService.jweDecrypt(jweEncrypted,
                new SingleX25519KeySource(octetKeyPair));
        // compare normalized
        assertArrayEquals(jweDecrypted, recordBytes);
    }

    @Test
    void testJWEDecryptStaticFromJava() throws IOException {
        // read key
        final OctetKeyPair octetKeyPair = readKey(
                "./src/test/resources/dart-test-data/z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT-keypairs.json");
        // read java jwe
        String jweEncrypted = ReadFileUtils.readString(
                "./src/test/resources/java-test-data/z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT-did.json.jwe");
        // decrypt java jwe
        byte[] jweDecrypted = CryptoService.x25519JweService.jweDecrypt(jweEncrypted,
                new SingleX25519KeySource(octetKeyPair));
        // load plain
        String recordJson = ReadFileUtils.readString(
                "./src/test/resources/dart-test-data/z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT-did.json");
        byte[] recordBytes = Base64URL.jcs_utf8_base64url(recordJson).getBytes(StandardCharsets.US_ASCII);
        // compare normalized
        assertArrayEquals(jweDecrypted, recordBytes);
    }

    @Test
    @Disabled
    void testJWEDecryptStaticFromDart() throws IOException {
        // read key
        final OctetKeyPair octetKeyPair = readKey(
                "./src/test/resources/dart-test-data/z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT-keypairs.json");
        // read dart jwe
        String jweEncrypted = ReadFileUtils.readString(
                "./src/test/resources/dart-test-data/z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT-did.json.jwe");
        // decrypt dart jwe
        byte[] jweDecrypted = CryptoService.x25519JweService.jweDecrypt(jweEncrypted,
                new SingleX25519KeySource(octetKeyPair));
        // load plain
        String recordJson = ReadFileUtils.readString(
                "./src/test/resources/dart-test-data/z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT-did.json");
        byte[] recordBytes = Base64URL.jcs_utf8_base64url(recordJson).getBytes(StandardCharsets.US_ASCII);
        // compare normalized
        assertArrayEquals(jweDecrypted, recordBytes);
    }

    private OctetKeyPair readKey(String file) throws IOException {
        String keypairs = ReadFileUtils.readString(
                "./src/test/resources/dart-test-data/z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT-keypairs.json");
        JsonNode node = JSON.MAPPER.readTree(keypairs);
        return JSON.MAPPER.convertValue(node.get("x25519"), OctetKeyPair.class);
    }

}
