package com.plooh.adssi.dial.crypto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.crypto.tink.subtle.Hkdf;
import com.google.crypto.tink.subtle.X25519;
import com.plooh.adssi.dial.data.OctetKeyPair;
import com.plooh.adssi.dial.data.OctetPublicKey;
import com.plooh.adssi.dial.encode.Base64URL;
import com.plooh.adssi.dial.jcs.JCS;
import com.plooh.adssi.dial.json.JSON;
import com.plooh.adssi.dial.key.KeySource;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;

public class X25519JweService {
    static final String _curve = "x25519";

    static final String header_key = "header";
    static final String nonce_key = "nonce";
    static final String cipherText_key = "cipherText";
    static final String authTag_key = "authTag";
    static final Random random = new SecureRandom();
    static final int secretKeyLength = 32;

    public String jweEncrypt(final byte[] clearText, final OctetPublicKey octetPublicKey) {
        if (!_curve.equals(octetPublicKey.getCurve())) {
            throw new IllegalArgumentException("X25519Encrypter only supports OctetKeyPairs with crv=X25519");
        }

        final Map<String, Object> header = _jweHeader(octetPublicKey.getKid());
        Map<String, String> jweParts;
        try {
            jweParts = _jweEncrypt(clearText, header, octetPublicKey);
        } catch (JsonProcessingException | GeneralSecurityException | IllegalStateException
                | InvalidCipherTextException e) {
            throw new RuntimeException(e);
        }

        final String headerPart = jweParts.get(header_key);
        final String noncePart = jweParts.get(nonce_key);
        final String cipherTextPart = jweParts.get(cipherText_key);
        final String authTagPart = jweParts.get(authTag_key);
        if (headerPart == null || noncePart == null || cipherTextPart == null || authTagPart == null) {
            throw new IllegalStateException("Missing jwe part");
        }
        return headerPart + ".." + noncePart + "." + cipherTextPart + "." + authTagPart;
    }

    public byte[] jweDecrypt(final String jweString, final KeySource keySource) {
        final String[] jweParts = _parseJWE(jweString);
        final String headerJcs64Url = jweParts[0];
        final String nonce64Url = jweParts[1];
        final String cipherText64Url = jweParts[2];
        final String authTag64Url = jweParts[3];
        try {
            return _jweDecrypt(headerJcs64Url, nonce64Url, cipherText64Url, authTag64Url, keySource);
        } catch (IOException | GeneralSecurityException | IllegalStateException | InvalidCipherTextException e) {
            throw new RuntimeException(e);
        }
    }

    private String[] _parseJWE(final String jweString) {
        return StringUtils.split(jweString, ".");
    }

    public JsonNode parseHeader(final String jweString) {
        try {
            return JSON.MAPPER.readTree(Base64URL.decode_pad_utf8_base64Url(_parseJWE(jweString)[0]));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, Object> _jweHeader(final String kid) {
        return Map.of("alg", "ECDH-ES", "enc", "A256GCM-HS256", "kid", kid);
    }

    private static Map<String, String> _jweEncrypt(final byte[] clearText, final Map<String, Object> header,
            final OctetPublicKey octetPublicKey) throws JsonProcessingException, GeneralSecurityException,
            IllegalStateException, InvalidCipherTextException {
        // Generate ephemeral X25519 key pair
        final String ephemeralKid = UUID.randomUUID().toString();
        final OctetKeyPair ephemeralKeyPair = CryptoService.x25519KeyService.genKeyPair(ephemeralKid);
        final OctetPublicKey ephemeralOctetPublicKey = ephemeralKeyPair.getPublicKey();
        final Map<String, String> ephemeralPublicKeyJWK = new HashMap<>();
        ephemeralPublicKeyJWK.put("kty", "OKP");
        ephemeralPublicKeyJWK.put("crv", "X25519");
        ephemeralPublicKeyJWK.put("x", ephemeralOctetPublicKey.getX());
        // Add the ephemeral public EC key to the header
        final Map<String, Object> newHeader = new HashMap<>(header);
        newHeader.put("epk", ephemeralPublicKeyJWK);
        // CryptoService.x25519KeyService.getPublicKeyBytes(publicKey)
        // final publicKey = x25519KeyService.toPublicKey(octetPublicKey);
        // Derive "Z"
        final byte[] sharedSecret = X25519.computeSharedSecret(
                CryptoService.x25519KeyService.getPrivateKeyBytes(ephemeralKeyPair),
                CryptoService.x25519KeyService.getPublicKeyBytes(octetPublicKey));
        return _encryptWithSharedSecret(newHeader, sharedSecret, clearText);
    }

    /// Encrypts the specified plaintext using the specified shared secret
    /// ("Z") and, if provided, the content encryption key (CEK).
    /// Implementation foccussed on "alg": "ECDH-ES", and "enc": "A128CBC-HS256",
    /// Direct Encryption
    private static Map<String, String> _encryptWithSharedSecret(final Map<String, Object> header,
            final byte[] sharedSecret, final byte[] clearText) throws JsonProcessingException, GeneralSecurityException,
            IllegalStateException, InvalidCipherTextException {
        // Derive shared key

        final byte[] nonce = new byte[12];
        random.nextBytes(nonce);
        // share secret derived using hkdf with a 96 bits nonce.
        // only direct mode (No Key wrapping)
        final byte[] cek = _deriveSharedKey(sharedSecret, nonce);
        return _auhtEncryptJWE(header, clearText, cek, nonce);
    }

    private static byte[] _deriveSharedKey(final byte[] sharedSecret, final byte[] nonce)
            throws GeneralSecurityException {
        // share secret derived using hkdf with a 96 bits nonce.
        // only direct mode (No Key wrapping)
        return Hkdf.computeHkdf("HMACSHA256", sharedSecret, nonce, null, 32);
    }

    private static Map<String, String> _auhtEncryptJWE(final Map<String, Object> header, final byte[] clearText,
            final byte[] cek, byte[] nonce)
            throws JsonProcessingException, IllegalStateException, InvalidCipherTextException {
        // BASE64URL-encoded JWE header.
        final String jcs_utf8_base64urlHeader = Base64URL.encode_base64Url_utf8_nopad(
                (JCS.encode(JSON.MAPPER.writeValueAsString(header))).getBytes(StandardCharsets.UTF_8));
        // Computes the Additional Authenticated Data (AAD) for the specified
        final byte[] aad = jcs_utf8_base64urlHeader.getBytes(StandardCharsets.US_ASCII);

        // Encrypt the plain text according to the JWE ENC
        // 16 bytes iv size (128 bits standard)
        final GCMBlockCipher _cypher = new GCMBlockCipher(new AESEngine());
        _cypher.init(true, new AEADParameters(new KeyParameter(cek), 128, nonce, aad));
        // Canonicalization good for innteroperability. We enforce json
        // base64Url hids special characters.
        // ascii takes base64 bytes to encryption safe bytes.
        final byte[] cipherBytesWMac = process(_cypher, clearText);
        final byte[] mac = _cypher.getMac();
        final byte[] cipherBytes = removeMac(cipherBytesWMac, mac);
        // X25519 no encrypted key as encryption key is derived. So position
        // JWE will look like: header..nonce.cipherText.authTag
        return Map.of(header_key, jcs_utf8_base64urlHeader, nonce_key, Base64URL.encode_base64Url_utf8_nopad(nonce),
                cipherText_key, Base64URL.encode_base64Url_utf8_nopad(cipherBytes), authTag_key,
                Base64URL.encode_base64Url_utf8_nopad(mac));
    }

    static byte[] _jweDecrypt(final String headerJcs64Url, final String nonce64Url, final String cipherText64Url,
            final String authTag64Url, KeySource keySource)
            throws IOException, GeneralSecurityException, IllegalStateException, InvalidCipherTextException {
        // Read ephemeral X25519 key pair
        final JsonNode header = JSON.MAPPER.readTree(Base64URL.decode_pad_utf8_base64Url(headerJcs64Url));
        final JsonNode ephemeralPublicKeyJWK = header.get("epk");
        final String kid = header.get("kid").asText();
        final OctetKeyPair octetKeyPair = keySource.x25519(kid);
        final byte[] keyPair = CryptoService.x25519KeyService.getPrivateKeyBytes(octetKeyPair);
        final String ephemeralPublicKey64Url = ephemeralPublicKeyJWK.get("x").asText();
        final byte[] ephemeralPublicKey = CryptoService.x25519KeyService
                .publicKeyFromBase64Url(ephemeralPublicKey64Url);
        // Derive "Z"
        final byte[] sharedSecret = X25519.computeSharedSecret(keyPair, ephemeralPublicKey);
        return _decryptWithSharedSecret(headerJcs64Url, sharedSecret, nonce64Url, cipherText64Url, authTag64Url);
    }

    /// Decrypts the encrypted JWE parts using the specified shared secret ("Z").
    /// No Compression
    static byte[] _decryptWithSharedSecret(final String headerJcs64Url, final byte[] sharedSecret,
            final String nonce64Url, final String cipherText64Url, final String authTag64Url)
            throws GeneralSecurityException, IllegalStateException, InvalidCipherTextException {
        // Derive shared key
        final byte[] cek = _deriveSharedKey(sharedSecret, Base64URL.decode_pad_utf8_base64Url(nonce64Url));
        return _decryptAuthJWE(headerJcs64Url, cek, nonce64Url, cipherText64Url, authTag64Url);
    }

    static byte[] _decryptAuthJWE(final String jcs_utf8_base64urlHeader, final byte[] cek, final String nonce64Url,
            final String cipherText64Url, final String authTag64Url)
            throws IllegalStateException, InvalidCipherTextException {
        final byte[] aad = jcs_utf8_base64urlHeader.getBytes(StandardCharsets.US_ASCII);
        final byte[] nonce = Base64URL.decode_pad_utf8_base64Url(nonce64Url);

        // Encrypt the plain text according to the JWE ENC
        // 16 bytes iv size (128 bits standard)
        final GCMBlockCipher _cypher = new GCMBlockCipher(new AESEngine());
        _cypher.init(false, new AEADParameters(new KeyParameter(cek), 128, nonce, aad));
        final byte[] cipherBytes = Base64URL.decode_pad_utf8_base64Url(cipherText64Url);
        final byte[] macBytes = Base64URL.decode_pad_utf8_base64Url(authTag64Url);
        final byte[] cipherBytesWMac = new byte[cipherBytes.length + macBytes.length];
        System.arraycopy(cipherBytes, 0, cipherBytesWMac, 0, cipherBytes.length);
        System.arraycopy(macBytes, 0, cipherBytesWMac, cipherBytes.length, macBytes.length);
        byte[] processed = process(_cypher, cipherBytesWMac);
        return processed;
    }

    private static byte[] process(GCMBlockCipher cypher, byte[] data)
            throws IllegalStateException, InvalidCipherTextException {
        final byte[] out = new byte[cypher.getOutputSize(data.length)];
        int len = cypher.processBytes(data, 0, data.length, out, 0);
        len += cypher.doFinal(out, len);
        final byte[] result = new byte[len];
        System.arraycopy(out, 0, result, 0, len);
        return result;
    }

    private static byte[] removeMac(byte[] cipherBytesWMac, byte[] mac) {
        final byte[] cipherBytes = new byte[cipherBytesWMac.length - mac.length];
        System.arraycopy(cipherBytesWMac, 0, cipherBytes, 0, cipherBytesWMac.length - mac.length);
        return cipherBytes;
    }
}
