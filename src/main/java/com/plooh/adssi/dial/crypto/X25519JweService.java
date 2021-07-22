package com.plooh.adssi.dial.crypto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.crypto.tink.subtle.AesCtrJceCipher;
import com.google.crypto.tink.subtle.Hkdf;
import com.google.crypto.tink.subtle.X25519;
import com.plooh.adssi.dial.data.OctetKeyPair;
import com.plooh.adssi.dial.data.OctetPublicKey;
import com.plooh.adssi.dial.encode.Base64URL;
import com.plooh.adssi.dial.jcs.JCS;
import com.plooh.adssi.dial.json.JSON;
import com.plooh.adssi.dial.key.KeySource;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

// final cypher = DartAesCbc(macAlgorithm: DartHmac(sha256));
// final _cypher=DartAesCtr(macAlgorithm:DartHmac(sha256));

public class X25519JweService {
    static final String _curve = "x25519";

    static final String header_key = "header";
    static final String nonce_key = "nonce";
    static final String cipherText_key = "cipherText";
    static final String authTag_key = "authTag";
    static final Random random = new SecureRandom();
    static final int secretKeyLength = 32;

    public String jweEncrypt(final byte[] clearText, final OctetPublicKey octetPublicKey) {
        if (_curve != octetPublicKey.getCurve()) {
            throw new IllegalArgumentException("X25519Encrypter only supports OctetKeyPairs with crv=X25519");
        }

        final Map<String, Object> header = _jweHeader(octetPublicKey.getKid());
        Map<String, String> jweParts;
        try {
            jweParts = _jweEncrypt(clearText, header, octetPublicKey);
        } catch (JsonProcessingException | GeneralSecurityException e) {
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

    byte[] jweDecrypt(final String jweString, final KeySource keySource) {
        final String[] jweParts = _parseJWE(jweString);
        final String headerJcs64Url = jweParts[0];
        final String nonce64Url = jweParts[2];
        final String cipherText64Url = jweParts[3];
        final String authTag64Url = jweParts[4];
        try {
            return _jweDecrypt(headerJcs64Url, nonce64Url, cipherText64Url, authTag64Url, keySource);
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    String[] _parseJWE(final String jweString) {
        return StringUtils.split(jweString, ".");
    }

    JsonNode parseHeader(final String jweString) {
        try {
            return JSON.MAPPER.readTree(Base64URL.decode_pad_utf8_base64Url(_parseJWE(jweString)[0]));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    String parseNonce(final String jweString) {
        return _parseJWE(jweString)[2];
    }

    static Map<String, Object> _jweHeader(final String kid) {
        return Map.of("alg", "ECDH-ES", "enc", "A256GCM-HS256", "kid", kid);
        // final Map<String, Object> jweHeader = new HashMap<>();
        // jweHeader.put("alg", "ECDH-ES");
        // jweHeader.put("enc", "A256GCM-HS256");
        // jweHeader.put("kid", kid);
        // return jweHeader;
    }

    static Map<String, String> _jweEncrypt(final byte[] clearText, final Map<String, Object> header,
            final OctetPublicKey octetPublicKey) throws JsonProcessingException, GeneralSecurityException {
        // Generate ephemeral X25519 key pair
        final String ephemeralKid = UUID.randomUUID().toString();
        final OctetKeyPair ephemeralKeyPair = CryptoService.x25519KeyService.genKeyPair(ephemeralKid);
        final OctetPublicKey ephemeralOctetPublicKey = ephemeralKeyPair.getPublicKey();
        final Map<String, String> ephemeralPublicKeyJWK = new HashMap<>();
        ephemeralPublicKeyJWK.put("kty", "OKP");
        ephemeralPublicKeyJWK.put("crv", "X25519");
        ephemeralPublicKeyJWK.put("x", ephemeralOctetPublicKey.getX());
        // Add the ephemeral public EC key to the header
        header.put("epk", ephemeralPublicKeyJWK);
        // CryptoService.x25519KeyService.getPublicKeyBytes(publicKey)
        // final publicKey = x25519KeyService.toPublicKey(octetPublicKey);
        // Derive "Z"
        final byte[] sharedSecret = X25519.computeSharedSecret(
                CryptoService.x25519KeyService.getPrivateKeyBytes(ephemeralKeyPair),
                CryptoService.x25519KeyService.getPublicKeyBytes(octetPublicKey));
        return _encryptWithSharedSecret(header, sharedSecret, clearText);
    }

    /// Encrypts the specified plaintext using the specified shared secret
    /// ("Z") and, if provided, the content encryption key (CEK).
    /// Implementation foccussed on "alg": "ECDH-ES", and "enc": "A128CBC-HS256",
    /// Direct Encryption
    static Map<String, String> _encryptWithSharedSecret(final Map<String, Object> header, final byte[] sharedSecret,
            final byte[] clearText) throws JsonProcessingException, GeneralSecurityException {
        // Derive shared key

        final byte[] nonce = new byte[12];
        random.nextBytes(nonce);
        // share secret derived using hkdf with a 96 bits nonce.
        // only direct mode (No Key wrapping)
        final byte[] cek = _deriveSharedKey(sharedSecret, nonce);
        return _auhtEncryptJWE(header, clearText, cek, nonce);
    }

    static byte[] _deriveSharedKey(final byte[] sharedSecret, final byte[] nonce) throws GeneralSecurityException {
        // share secret derived using hkdf with a 96 bits nonce.
        // only direct mode (No Key wrapping)
        return Hkdf.computeHkdf("HMACSHA256", sharedSecret, nonce, null, 32);
    }

    static Map<String, String> _auhtEncryptJWE(final Map<String, Object> header, final byte[] clearText,
            final byte[] cek, byte[] nonce) throws GeneralSecurityException, JsonProcessingException {
        // BASE64URL-encoded JWE header.
        final String jcs_utf8_base64urlHeader = Base64URL.encode_base64Url_utf8_nopad(
                (JCS.encode(JSON.MAPPER.writeValueAsString(header))).getBytes(StandardCharsets.UTF_8));
        // Computes the Additional Authenticated Data (AAD) for the specified
        // final byte[] aad =
        // jcs_utf8_base64urlHeader.getBytes(StandardCharsets.US_ASCII);

        // Encrypt the plain text according to the JWE ENC
        // 16 bytes iv size (128 bits standard)
        AesCtrJceCipher _cypher = new AesCtrJceCipher(cek, 16);
        byte[] cipherBytes = _cypher.encrypt(clearText);
        // final secretBox = _cypher.encrypt(clearText, secretKey: cek, nonce: nonce);
        // //, aad: aad);

        byte[] mac = _mac(cek, clearText);

        // X25519 no encrypted key as encryption key is derived. So position
        // JWE will look like: header..nonce.cipherText.authTag
        return Map.of("header_key", jcs_utf8_base64urlHeader, "nonce_key", Base64URL.encode_base64Url_utf8_nopad(nonce),
                "cipherText_key", Base64URL.encode_base64Url_utf8_nopad(cipherBytes), "authTag_key",
                Base64URL.encode_base64Url_utf8_nopad(mac));
    }

    static byte[] _mac(byte[] key, byte[] content) {
        HMac hmac = new HMac(new SHA256Digest());
        hmac.init(new KeyParameter(key));
        hmac.update(content, 0, content.length);
        byte[] mac = new byte[hmac.getMacSize()];
        int len = hmac.doFinal(mac, 0);
        return mac;
    }

    static byte[] _jweDecrypt(final String headerJcs64Url, final String nonce64Url, final String cipherText64Url,
            final String authTag64Url, KeySource keySource) throws IOException, GeneralSecurityException {
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
            throws GeneralSecurityException {
        // Derive shared key
        final byte[] cek = _deriveSharedKey(sharedSecret, Base64URL.decode_pad_utf8_base64Url(nonce64Url));
        return _decryptAuthJWE(headerJcs64Url, cek, nonce64Url, cipherText64Url, authTag64Url);
    }

    static byte[] _decryptAuthJWE(final String jcs_utf8_base64urlHeader, final byte[] cek, final String nonce64Url,
            final String cipherText64Url, final String authTag64Url) throws GeneralSecurityException {
        // TODO research on why authenticcated encryption is not accepted in the native
        // hashmac implementation DartHmac.
        // Computes the Additional Authenticated Data (AAD) for the specified
        // final aad = ascii.encode(jcs_utf8_base64urlHeader);

        AesCtrJceCipher _cypher = new AesCtrJceCipher(cek, 16);
        byte[] plainBytes = _cypher.decrypt(Base64URL.decode_pad_utf8_base64Url(cipherText64Url));
        byte[] computedMac = _mac(cek, plainBytes);
        byte[] providedMac = Base64URL.decode_pad_utf8_base64Url(authTag64Url);
        if (!Arrays.equals(computedMac, providedMac)) {
            throw new IllegalStateException("Message authentication code not available.");
        }
        return plainBytes;
    }
}
