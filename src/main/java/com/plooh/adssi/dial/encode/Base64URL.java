package com.plooh.adssi.dial.encode;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.plooh.adssi.dial.jcs.JCS;

import org.apache.commons.lang3.StringUtils;

public class Base64URL {
    public static String encode_base64Url_utf8_nopad(byte[] bytes) {
        return StringUtils.substringBefore(new String(Base64.getUrlEncoder().encode(bytes), StandardCharsets.UTF_8),
                "=");
    }

    public static byte[] decode_pad_utf8_base64Url(String value) {
        int paddedLength = value.length() + (4 - (value.length() % 4)) % 4;
        String rightPadded = StringUtils.rightPad(value, paddedLength, "=");
        return Base64.getUrlDecoder().decode(rightPadded);
    }

    public static String jcs_utf8_base64url(final String input) {
        final byte[] canonicalUtf8Bytes = JCS.encode_utf8(input);
        return encode_base64Url_utf8_nopad(canonicalUtf8Bytes);
    }

    public static String base64url_utf8_jcs(final String input) {
        final byte[] utf8Bytes = decode_pad_utf8_base64Url(input);
        final String utf8String = new String(utf8Bytes, StandardCharsets.UTF_8);
        final String canonicalString = JCS.encode(utf8String);
        return canonicalString;
    }
}