package com.plooh.adssi.dial.crypto;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;

public class CryptoUtils {

    static byte[] sha256(byte[] input) {
        return computeDigest(input, new SHA256Digest());
    }

    public static byte[] ripemd160(byte[] input) {
        return computeDigest(input, new RIPEMD160Digest());
    }

    public static byte[] sah256Ripemd160(byte[] input) {
        return ripemd160(sha256(input));
    }

    private static byte[] computeDigest(byte[] input, Digest digest) {
        if (input == null) {
            throw new NullPointerException("Can't hash a NULL value");
        }
        digest.update(input, 0, input.length);
        byte[] result = new byte[digest.getDigestSize()];
        digest.doFinal(result, 0);
        return result;
    }
}