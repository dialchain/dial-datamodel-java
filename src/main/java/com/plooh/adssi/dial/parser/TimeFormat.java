package com.plooh.adssi.dial.parser;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TimeFormat {
    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_INSTANT;

    public static String format(Instant now) {
        if (now == null)
            return null;
        return DTF.format(now.truncatedTo(ChronoUnit.SECONDS));
    }
}