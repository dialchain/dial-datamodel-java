package com.plooh.adssi.dial.crypto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import com.plooh.adssi.dial.data.ECKeyPair;
import com.plooh.adssi.dial.data.EncodedECPublicKey;

import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.Test;

public class BitcoinAddressTest {
    public static Secp256k1VerificationKey2021Service keyService = Secp256k1VerificationKey2021Service.instance;

    @Test
    void testP2wpkhAddress() {
        ECKeyPair keyPair = keyService.genKeyPair("Simple Key");

        String p2wpkhAddressTest = BitcoinAddress.p2wpkhAddress(TestNet3Params.get(),
                keyService.publicKeyEnncoded(keyPair.getPublicKey()));
        assertTrue(p2wpkhAddressTest.startsWith("tb1"));

        String p2wpkhAddressMain = BitcoinAddress.p2wpkhAddress(MainNetParams.get(),
                keyService.publicKeyEnncoded(keyPair.getPublicKey()));
        assertTrue(p2wpkhAddressMain.startsWith("bc1"));
    }

    @Test
    void testP2shAddress() {
        ECKeyPair keyPair0 = keyService.genKeyPair("Simple Key - 0");
        ECKeyPair keyPair1 = keyService.genKeyPair("Simple Key - 1");
        ECKeyPair keyPair2 = keyService.genKeyPair("Simple Key - 2");
        EncodedECPublicKey ecPublicKey0 = keyService.publicKeyEnncoded(keyPair0.getPublicKey());
        EncodedECPublicKey ecPublicKey1 = keyService.publicKeyEnncoded(keyPair1.getPublicKey());
        EncodedECPublicKey ecPublicKey2 = keyService.publicKeyEnncoded(keyPair2.getPublicKey());
        List<EncodedECPublicKey> keyList = Arrays.asList(ecPublicKey0, ecPublicKey1, ecPublicKey2);

        String p2shAddressTest = BitcoinAddress.p2shAddress(TestNet3Params.get(), 2, keyList);
        assertTrue(p2shAddressTest.startsWith("2"));

        String p2shAddressMain = BitcoinAddress.p2shAddress(MainNetParams.get(), 2, keyList);
        assertTrue(p2shAddressMain.startsWith("3"));
    }

}
