package com.plooh.adssi.dial.cid;

import io.ipfs.multibase.Multibase;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.plooh.adssi.dial.jcs.JCS;

import io.ipfs.cid.Cid;
import io.ipfs.multihash.Multihash;

public class CidUtils {

    private static MessageDigest hasher;
    static {
        try {
            hasher = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String jcsCidB58(String fileContentUtf8) {
        String cannonicalString = JCS.encode(fileContentUtf8);
        byte[] hash = hasher.digest(cannonicalString.getBytes(StandardCharsets.UTF_8));
        Cid cid = new Cid(1L, Cid.Codec.Raw, Multihash.Type.sha2_256, hash);
        return Multibase.encode(Multibase.Base.Base58BTC, cid.toBytes());
    }

    public static String jcsCidB32(String fileContentUtf8) {
        String cannonicalString = JCS.encode(fileContentUtf8);
        byte[] hash = hasher.digest(cannonicalString.getBytes(StandardCharsets.UTF_8));
        Cid cid = new Cid(1L, Cid.Codec.Raw, Multihash.Type.sha2_256, hash);
        return Multibase.encode(Multibase.Base.Base32, cid.toBytes());
    }

}