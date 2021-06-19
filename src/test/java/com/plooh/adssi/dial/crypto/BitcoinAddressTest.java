package com.plooh.adssi.dial.crypto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.interfaces.ECPublicKey;
import java.util.Arrays;
import java.util.List;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.KeyUse;

import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.Test;

public class BitcoinAddressTest {
    public static CommonECKeyService keyService = new CommonECKeyService(Curve.SECP256K1, KeyUse.SIGNATURE);

    @Test
    void testP2wpkhAddress() throws JOSEException {
        ECKey keyPair = keyService.genKeyPair("Simple Key");

        String p2wpkhAddressTest = BitcoinAddress.p2wpkhAddress(TestNet3Params.get(), keyPair.toECPublicKey());
        assertTrue(p2wpkhAddressTest.startsWith("tb1"));

        String p2wpkhAddressMain = BitcoinAddress.p2wpkhAddress(MainNetParams.get(), keyPair.toECPublicKey());
        assertTrue(p2wpkhAddressMain.startsWith("bc1"));
    }

    @Test
    void testP2shAddress() throws JOSEException {
        ECKey keyPair0 = keyService.genKeyPair("Simple Key - 0");
        ECKey keyPair1 = keyService.genKeyPair("Simple Key - 1");
        ECKey keyPair2 = keyService.genKeyPair("Simple Key - 2");
        ECPublicKey ecPublicKey0 = keyPair0.toECPublicKey();
        ECPublicKey ecPublicKey1 = keyPair1.toECPublicKey();
        ECPublicKey ecPublicKey2 = keyPair2.toECPublicKey();
        List<ECPublicKey> keyList = Arrays.asList(ecPublicKey0, ecPublicKey1, ecPublicKey2);

        String p2shAddressTest = BitcoinAddress.p2shAddress(TestNet3Params.get(), 2, keyList);
        assertTrue(p2shAddressTest.startsWith("2"));

        String p2shAddressMain = BitcoinAddress.p2shAddress(MainNetParams.get(), 2, keyList);
        assertTrue(p2shAddressMain.startsWith("3"));
    }

}
