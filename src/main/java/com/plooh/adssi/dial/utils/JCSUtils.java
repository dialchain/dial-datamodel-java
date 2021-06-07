package com.plooh.adssi.dial.utils;

import java.io.IOException;

import org.erdtman.jcs.JsonCanonicalizer;

public class JCSUtils {
    public static String encode(String l) {
        try {
            return new JsonCanonicalizer(l).getEncodedString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}