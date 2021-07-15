package com.plooh.adssi.dial.jcs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.erdtman.jcs.JsonCanonicalizer;

public class JCS {
    public static String encode(String l) {
        try {
            return new JsonCanonicalizer(l).getEncodedString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] encode_utf8(String l) {
        return encode(l).getBytes(StandardCharsets.UTF_8);
    }
}