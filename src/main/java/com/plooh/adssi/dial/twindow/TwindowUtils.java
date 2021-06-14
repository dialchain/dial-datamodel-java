package com.plooh.adssi.dial.twindow;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import com.plooh.adssi.dial.data.Twindow;

public class TwindowUtils {
    public static final DateTimeFormatter DTF = DateTimeFormatter.ISO_INSTANT;

    public static final Twindow twindow(Instant now, String antecedentHash, String antecedentClosing) {
        Twindow twindow = twindow(now);
        twindow.setA_hash(antecedentHash);
        twindow.setA_closing(antecedentClosing);
        return twindow;
    }

    public static final Twindow twindow(Instant now) {
        Instant start = now.truncatedTo(ChronoUnit.DAYS);
        Instant end = start.plus(1, ChronoUnit.DAYS);
        String startFormated = TwindowUtils.DTF.format(start);
        String endFormated = TwindowUtils.DTF.format(end);

        Twindow twindow = new Twindow();
        twindow.setStart(startFormated);
        twindow.setEnd(endFormated);
        return twindow;
    }

    public static final Instant openingTwindowRecordDate(Instant now) {
        return now.truncatedTo(ChronoUnit.DAYS);
    }

    public static final Instant closingTwindowRecordDate(Instant now) {
        return now.truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS);
    }

    public static final Twindow antecedantTwindow(String antecedantClosing) {
        Instant start = Instant.parse(antecedantClosing).minus(1, ChronoUnit.DAYS);
        String startFormated = TwindowUtils.DTF.format(start);
        Twindow twindow = new Twindow();
        twindow.setStart(startFormated);
        twindow.setEnd(antecedantClosing);
        return twindow;
    }
}