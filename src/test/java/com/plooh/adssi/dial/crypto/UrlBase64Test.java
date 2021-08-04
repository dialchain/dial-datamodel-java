package com.plooh.adssi.dial.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.UrlBase64;
import org.junit.jupiter.api.Test;

public class UrlBase64Test {
    @Test
    void fromUrlBase64() {
        String s = "ChDL8ymvQHjb9iiY5tVTH1nkZy87KAmbTffUe";
        byte[] encodedBytes = UrlBase64.encode(s.getBytes(StandardCharsets.UTF_8));
        String encodedString = new String(encodedBytes);
        assertTrue(encodedString.contains("."));
        String encodedNoPadString = StringUtils.substringBefore(encodedString, ".");
        assertEquals(encodedNoPadString, "Q2hETDh5bXZRSGpiOWlpWTV0VlRIMW5rWnk4N0tBbWJUZmZVZQ");
        int b = encodedNoPadString.length() + (4 - (encodedNoPadString.length() % 4));
        String rightPadded = StringUtils.rightPad(encodedNoPadString, b, ".");
        byte[] decodedBytes = UrlBase64.decode(rightPadded);
        String decodedString = new String(decodedBytes);
        assertEquals(decodedString, s);
    }
}