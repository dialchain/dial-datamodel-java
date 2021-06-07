package com.plooh.adssi.dial.crypto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import com.plooh.adssi.dial.utils.JCSUtils;
import com.plooh.adssi.dial.utils.JsonUtils;

public class JcsBase64Ed25519Signature2021Service {

    public static final String SIGNATURE_VALUE_FIELD = "signatureValue";
    public static final String SIGNATURE_TYPE_FIELD = "type";
    public static final String SIGNATURE_TYPE = "JcsBase64Ed25519Signature2021";
    public static final TypeReference<HashMap<String, Object>> typeRefMap = new TypeReference<HashMap<String, Object>>() {
    };

    public static boolean verify(String data, OctetKeyPair publicJWK, Map<String, Object> headerParams) {
        try {
            return verifyInternal(data, publicJWK, headerParams);
        } catch (JOSEException | ParseException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> sign(String data, OctetKeyPair keyPair, Map<String, Object> headerParams) {
        try {
            return signInternal(data, keyPair, headerParams);
        } catch (IOException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, Object> signInternal(String datJson, OctetKeyPair keyPair,
            Map<String, Object> headerParams) throws IOException, JOSEException {
        // header payload
        HashMap<String, Object> signHeaderData = new HashMap<String, Object>(headerParams);
        signHeaderData.remove(SIGNATURE_VALUE_FIELD);
        signHeaderData.put(SIGNATURE_TYPE_FIELD, SIGNATURE_TYPE);
        String jwsHeaderJson = JsonUtils.MAPPER.writeValueAsString(signHeaderData);
        String jwsHeaderJCSBase64 = jcs_utf8_base64url(jwsHeaderJson).toString();
        String jwsPayloadString = jcs_utf8_base64url(datJson).toString();
        byte[] signingInput = (jwsHeaderJCSBase64 + "." + jwsPayloadString).getBytes(StandardCharsets.UTF_8);

        // Ed25519 signature
        Ed25519Signer ed25519Signer = new Ed25519Signer(keyPair);
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.EdDSA).build();
        Base64URL sign = ed25519Signer.sign(jwsHeader, signingInput);
        HashMap<String, Object> result = JsonUtils.MAPPER.readValue(jwsHeaderJson, typeRefMap);
        result.put(SIGNATURE_VALUE_FIELD, sign.toString());

        return result;
    }

    private static boolean verifyInternal(String dataJson, OctetKeyPair publicJWK, Map<String, Object> headerParams)
            throws JOSEException, JsonProcessingException, ParseException {

        HashMap<String, Object> signHeaderData = new HashMap<String, Object>(headerParams);
        signHeaderData.remove(SIGNATURE_VALUE_FIELD);
        signHeaderData.put(SIGNATURE_TYPE_FIELD, SIGNATURE_TYPE);
        // JWSHeader jwsHeader = new
        // JWSHeader.Builder(JWSAlgorithm.EdDSA).customParams(hashMap).build();
        String jwsHeaderJson = JsonUtils.MAPPER.writeValueAsString(signHeaderData);
        Base64URL jwsHeaderJCSBase64 = jcs_utf8_base64url(jwsHeaderJson);
        Base64URL jwsPayloadBase64 = jcs_utf8_base64url(dataJson);
        JWSObject jwsObject = new JWSObject(jwsHeaderJCSBase64, jwsPayloadBase64,
                Base64URL.from(headerParams.get(SIGNATURE_VALUE_FIELD).toString()));
        return jwsObject.verify(new Ed25519Verifier(publicJWK));
    }

    private static Base64URL jcs_utf8_base64url(final String input) {
        return Base64URL.encode(JCSUtils.encode(input));
    }
}
