package com.plooh.adssi.dial.key;

import com.plooh.adssi.dial.data.ECKeyPair;
import com.plooh.adssi.dial.data.OctetKeyPair;

public interface KeySource {
    OctetKeyPair ed25519(String keyId);

    OctetKeyPair x25519(String keyId);

    ECKeyPair secp256k1(String keyId);
}
