package com.plooh.adssi.dial.crypto;

import java.util.List;
import java.util.stream.Collectors;

import com.plooh.adssi.dial.data.EncodedECPublicKey;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.Script.ScriptType;
import org.bitcoinj.script.ScriptBuilder;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.FixedPointUtil;

public class BitcoinAddress {
    // The parameters of the secp256k1 curve that Bitcoin uses.
    private static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");
    /** The parameters of the secp256k1 curve that Bitcoin uses. */
    public static final ECDomainParameters CURVE;

    static {
        // Tell Bouncy Castle to precompute data that's needed during secp256k1
        // calculations.
        FixedPointUtil.precompute(CURVE_PARAMS.getG());
        CURVE = new ECDomainParameters(CURVE_PARAMS.getCurve(), CURVE_PARAMS.getG(), CURVE_PARAMS.getN(),
                CURVE_PARAMS.getH());
    }

    public static String p2wpkhAddress(NetworkParameters params, EncodedECPublicKey ecPublicKey) {
        return Address.fromKey(params, toEcKey(ecPublicKey), ScriptType.P2WPKH).toString();
    }

    public static String p2shAddress(NetworkParameters params, int threshold, List<EncodedECPublicKey> ecPublicKeys) {
        List<ECKey> keys = ecPublicKeys.stream().map(ecPublicKey -> toEcKey(ecPublicKey)).collect(Collectors.toList());
        Script redeemScript = ScriptBuilder.createRedeemScript(threshold, keys);
        Script script = ScriptBuilder.createP2SHOutputScript(redeemScript);
        Address multisig = script.getToAddress(params);
        return multisig.toString();
    }

    private static ECKey toEcKey(EncodedECPublicKey ecPublicKey) {
        return ECKey.fromPublicOnly(ecPublicKey.getBytes());
    }

}