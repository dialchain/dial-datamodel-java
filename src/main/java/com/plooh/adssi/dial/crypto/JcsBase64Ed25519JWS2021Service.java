package com.plooh.adssi.dial.crypto;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.crypto.tink.subtle.Ed25519Sign;
import com.google.crypto.tink.subtle.Ed25519Verify;
import com.plooh.adssi.dial.data.OctetKeyPair;
import com.plooh.adssi.dial.data.OctetPublicKey;
import com.plooh.adssi.dial.encode.Base64URL;
import com.plooh.adssi.dial.jcs.JCS;
import com.plooh.adssi.dial.json.JSON;

import org.apache.commons.lang3.StringUtils;

public class JcsBase64Ed25519JWS2021Service {

    public String signJWS(String data, OctetKeyPair octetKeyPair, boolean detachedPayload) {
        final String jcs_utf8_base64urlHeader = _jcsUtf8Base64urlHeader(octetKeyPair.getPublicKey().getKid());
        final String jcs_utf8_base64urlData = Base64URL
                .encode_base64Url_utf8_nopad(data.getBytes(StandardCharsets.UTF_8));
        final String signingInputString = jcs_utf8_base64urlHeader + '.' + jcs_utf8_base64urlData;

        try {
            // Sign
            Ed25519Sign ed25519Sign = new Ed25519Sign(CryptoService.ed25519KeyService.getPrivateKeyBytes(octetKeyPair));
            byte[] signatureBytes = ed25519Sign.sign(signingInputString.getBytes(StandardCharsets.UTF_8));

            // Produce signature b64 string
            return _jws(signatureBytes, detachedPayload, jcs_utf8_base64urlHeader, jcs_utf8_base64urlData);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    boolean verifyAttached(String jwsString, OctetPublicKey opk) {
        String[] split = StringUtils.split(jwsString, ".");
        final String base64urlHeader = split[0];
        final String base64urlData = split[1];
        final String signatureBe64URL = split[2];

        // Verify header matches spec
        final String jcs_utf8_base64urlHeader = _jcsUtf8Base64urlHeader(opk.getKid());
        if (!base64urlHeader.equals(jcs_utf8_base64urlHeader)) {
            throw new IllegalArgumentException("Bad input string. header not matching jcs_utf8_base64url Header");
        }
        return _verifyAll(jcs_utf8_base64urlHeader, base64urlData, signatureBe64URL, opk);
    }

    // private String[] _parseJWS(final String jwsString) {
    // return jwsString.split(".");
    // }

    // public JsonNode parseHeader(final String jwsString) {
    // try {
    // return JSON.MAPPER.readTree(
    // new String(Base64URL.decode_pad_utf8_base64Url(_parseJWS(jwsString)[0]),
    // StandardCharsets.UTF_8));
    // } catch (JsonProcessingException e) {
    // throw new RuntimeException(e);
    // }
    // }

    private boolean _verifyAll(String header64Url, String paylod64Url, String signature64Url,
            OctetPublicKey publicKey) {
        final String signingInputString = header64Url + "." + paylod64Url;
        final byte[] signatureBytes = Base64URL.decode_pad_utf8_base64Url(signature64Url);
        Ed25519Verify ed25519Verify = new Ed25519Verify(CryptoService.ed25519KeyService.getPublicKeyBytes(publicKey));
        try {
            ed25519Verify.verify(signatureBytes, signingInputString.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (GeneralSecurityException e) {
            // TODO: log
            return false;
        }
    }

    private String _jcsUtf8Base64urlHeader(String kid) {
        final Map<String, String> jwsHeader = new HashMap<>();
        jwsHeader.put("alg", "EdDSA");
        jwsHeader.put("kid", kid);
        try {
            return Base64URL.encode_base64Url_utf8_nopad(
                    JCS.encode(JSON.MAPPER.writeValueAsString(jwsHeader)).getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String _jws(byte[] signatureBytes, boolean detachedPayload, String jcs_utf8_base64urlHeader,
            String jcs_utf8_base64urlData) {
        final String base64encoded = Base64URL.encode_base64Url_utf8_nopad(signatureBytes);
        return detachedPayload ? jcs_utf8_base64urlHeader + '.' + '.' + base64encoded
                : jcs_utf8_base64urlHeader + '.' + jcs_utf8_base64urlData + '.' + base64encoded;
    }
}
