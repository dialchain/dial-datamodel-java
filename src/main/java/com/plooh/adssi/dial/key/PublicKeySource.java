package com.plooh.adssi.dial.key;

import com.plooh.adssi.dial.data.ECPublicKey;
import com.plooh.adssi.dial.data.OctetPublicKey;

public interface PublicKeySource {
    OctetPublicKey ed25519(String keyId);

    OctetPublicKey x25519(String keyId);

    ECPublicKey secp256k1(String keyId);
}
