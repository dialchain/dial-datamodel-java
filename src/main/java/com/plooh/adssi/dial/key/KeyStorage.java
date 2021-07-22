package com.plooh.adssi.dial.key;

import com.plooh.adssi.dial.data.ECKeyPair;
import com.plooh.adssi.dial.data.OctetKeyPair;

public interface KeyStorage {
    String getUid();

    OctetKeyPair ed25519(String keyId);

    OctetKeyPair x25519(String keyId);

    ECKeyPair secp256k1(String keyId);

    OctetKeyPair randomEd25519();

    OctetKeyPair randomX25519();

    ECKeyPair randomSecp256k1();
}
