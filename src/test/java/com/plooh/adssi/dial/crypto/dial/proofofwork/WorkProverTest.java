package com.plooh.adssi.dial.crypto.dial.proofofwork;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class WorkProverTest {

    NumberFormat formatter = new DecimalFormat("0.#####E0");

    @Test
    void shouldSolveChallenge() {
        String clearText = "Willie";
        int leadingZeros = 3;
        String hashPrefixGoal = StringUtils.repeat("0", leadingZeros);

        WorkProver.Result result = new WorkProver().solveChallenge(clearText, leadingZeros);

        log.info("\"" + clearText + "\" + \"" + result.getSuccessfulNonce() + "\"");
        log.info(result.getSuccessfulHash());
        log.info(" Time to solve: " + result.getTimeToSolveMS() + "ms ("
            + formatter.format(result.getSuccessfulNonceInt()) + " nonces)");

        Assertions.assertThat(result.getSuccessfulHash().substring(0, leadingZeros).equalsIgnoreCase(hashPrefixGoal)).isTrue();
    }

}
