package com.plooh.adssi.dial.jcs;

import java.io.IOException;

import org.erdtman.jcs.JsonCanonicalizer;

public class JCS {
    public static String encode(String l) {
        try {
            return new JsonCanonicalizer(l).getEncodedString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}