package com.plooh.adssi.dial.crypto.dial.proofofwork;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Class used for proof-of-work type computation using SHA256 hash to find a
 * nonce which solves for n leading zeroes (base64).
 */
@Slf4j
public class WorkProver {

    private static NumberFormat FORMATTER = new DecimalFormat("0.#####E0");
    private static MessageDigest messageDigest;

    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public WorkProver() {
    }

    /**
     * This method will take the challenge text, and brute force find an
     * appended nonce which will solve a SHA256 hash resulting in n leading
     * zeros (base 64).
     *
     * @param challengeText
     *            String. Text used in the challenge.
     * @param leadingZeros
     *            Integer. Number of desired leading zeros (base 64). The larger
     *            the number, the more complex the task.
     * @return String The nonce used to solve the problem.
     */
    public WorkProver.Result solveChallenge(String challengeText, int leadingZeros) {
        log.info("Clear text: [{}] and desired leading zeroes: [{}].%n",challengeText, leadingZeros);

        String hashPrefixGoal = StringUtils.repeat("0", leadingZeros);

        // Measure the time to succeed
        long startTime = System.nanoTime();

        int nonceInteger = 0;
        String currentNonce = getHexNonceFromInteger(nonceInteger);
        String currentHash = hashSHA256(challengeText + currentNonce);
        while (!currentHash.substring(0, leadingZeros).equalsIgnoreCase(hashPrefixGoal)) {
            nonceInteger += 1;
            currentNonce = getHexNonceFromInteger(nonceInteger);
            currentHash = hashSHA256(challengeText + currentNonce);

            if (nonceInteger % 500000 == 0) {
                // For seeing status
                log.debug(" # of nonces tried: {}.", FORMATTER.format(nonceInteger));
            }
        }

        long estimatedTime = System.nanoTime() - startTime;

        return WorkProver.Result.builder()
            .challengeText(challengeText)
            .successfulNonce(currentNonce)
            .successfulNonceInt(nonceInteger)
            .successfulHash(currentHash)
            .timeToSolveMS((int) Math.floor(estimatedTime / 1000000.0))
            .build();
    }

    /**
     * This method will take the challenge text and the successful nonce to computed
     *  the successful SHA256 hash resulting in n leading zeros (base 64).
     *
     * @param challengeText
     *            String. Text used in the challenge.
     * @param successfulNonce
     *            String. nonce used to solve the problem.
     * @param leadingZeros
     *            Integer. Number of desired leading zeros (base 64). The larger
     *            the number, the more complex the task.
     * @return Boolean true if the computed hash matches the desired leading zeros (base 64).
     */
    public Boolean validateChallenge(String challengeText, String successfulNonce, int leadingZeros) {
        log.info("Clear text: [{}], successful nonce: [{}] and desired leading zeroes: [{}].%n", challengeText, successfulNonce, leadingZeros);
        String currentHash = hashSHA256(challengeText + successfulNonce);

        String hashPrefixGoal = StringUtils.repeat("0", leadingZeros);
        return currentHash.substring(0, leadingZeros).equalsIgnoreCase(hashPrefixGoal);
    }

    /**
     * Method used to hash clearText using SHA256. Can be used to verify output
     * from solveChallenge() since this same method is used.
     *
     * @param clearText
     *            String. Text to be hashed.
     * @return String. 64 chars of SHA256 applied to clearText
     */
    public String hashSHA256(String clearText) {
        byte[] digest = messageDigest.digest(clearText.getBytes(StandardCharsets.UTF_8));
        String fx = "%0" + (digest.length*2) + "x";
        return String.format(fx, new BigInteger(1, digest));
    }

    private String getHexNonceFromInteger(int nonceInt) {
        return Integer.toHexString(nonceInt);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        private String challengeText;
        private int timeToSolveMS;
        private String successfulNonce;
        private int successfulNonceInt;
        private String successfulHash;
    }

}
