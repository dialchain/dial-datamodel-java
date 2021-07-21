package com.plooh.adssi.dial.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.plooh.adssi.dial.ReadFileUtils;
import com.plooh.adssi.dial.data.ECKeyPair;
import com.plooh.adssi.dial.data.EncodedECKey;
import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.data.SignatureResult;
import com.plooh.adssi.dial.jcs.JCS;
import com.plooh.adssi.dial.json.JSON;

import org.junit.jupiter.api.Test;

public class JcsBase64Secp256k1Signature2021ServiceTest {
    @Test
    void testVerifySignature() throws Exception {
        String keypairs = ReadFileUtils.readString(
                "./src/test/resources/dart-test-data/z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT-keypairs.json");
        String recordJson = ReadFileUtils.readString(
                "./src/test/resources/dart-test-data/z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT-half-signed.json");
        JsonNode node = JSON.MAPPER.readTree(keypairs);
        ECKeyPair eckeyPair = JSON.MAPPER.convertValue(node.get("secp256k1"), ECKeyPair.class);

        Proof template = Proof.builder().issuer("z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT")
                .created("2021-07-20T17:34:16.000Z").proofPurpose("PoP").type("JcsBase64Secp256k1Signature2021")
                .verificationMethod(
                        "z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT#2021-07-20T17:34:16.000Z#key-Secp256k1-1")
                .nonce("387837f7-25a0-4fef-9990-0a29c7873182").build();
        EncodedECKey ecKeyEncoded = CryptoService.secp256k1KeyService.ecKeyEncoded(eckeyPair, eckeyPair.getPublicKey());
        SignatureResult signDeclaration = CryptoService.secp256k1SignatureService.signDeclaration(recordJson, template,
                ecKeyEncoded);

        String fullRecord = ReadFileUtils.readString(
                "./src/test/resources/dart-test-data/z77ccjskADhJRP9oRY5cxb3RfuguAokdiMZUNEM4YjJrT-did.json");
        assertEquals(JCS.encode(signDeclaration.getSignedRecord()), JCS.encode(fullRecord));
    }
}
