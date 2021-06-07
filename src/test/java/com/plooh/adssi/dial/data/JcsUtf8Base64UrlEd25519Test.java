package com.plooh.adssi.dial.data;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class JcsUtf8Base64UrlEd25519Test {

    @Test
    public void testSignVerify() throws IOException {
        String keyID = "sampleKey";
        // OctetKeyPairz keyPair =
        // Ed25519VerificationKey2021Service.generateKeyPair(keyID);
        // InputStream is =z this.getClass().getResourceAsStream("/did001.json");
        // String data = IOUtils.readInputStreamToString(is, StandardCharsets.UTF_8);
        // String jws = JcsBase64Ed25519Signature2021Service.sign(data, keyPair);

        // String signatureBase64Url = StringUtils.substringAfterLast(jws, ".");

        // String publicKeyMultibase =
        // Ed25519VerificationKey2021Service.publicKeyMultibase(keyPair);

        // // // Verify
        // OctetKeyPair publicJWK =
        // Ed25519VerificationKey2021Service.publicKeyFromMultibase(publicKeyMultibase,
        // keyID);
        // boolean verified = JcsBase64Ed25519Signature2021Service.verify(data,
        // publicJWK, signatureBase64Url);
        // assertTrue(verified);
    }
}