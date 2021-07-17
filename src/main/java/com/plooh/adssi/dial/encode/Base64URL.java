package com.plooh.adssi.dial.encode;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.encoders.UrlBase64;

public class Base64URL {
    public static String encode_base64Url_utf8_nopad(byte[] bytes) {
        return StringUtils.substringBefore(new String(UrlBase64.encode(bytes), StandardCharsets.UTF_8), ".");
    }

    public static byte[] decode_pad_utf8_base64Url(String value) {
        int paddedLength = value.length() + (4 - (value.length() % 4)) % 4;
        String rightPadded = StringUtils.rightPad(value, paddedLength, ".");
        return UrlBase64.decode(rightPadded);
    }
}