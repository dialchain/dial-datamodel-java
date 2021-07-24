package com.plooh.adssi.dial.key;

import com.plooh.adssi.dial.data.ECKeyPair;
import com.plooh.adssi.dial.data.OctetKeyPair;

public class StorageKeySource implements KeySource {
    final KeyStorage _keyStorage;

    public StorageKeySource(KeyStorage keyStorage) {
        this._keyStorage = keyStorage;
    }

    @Override
    public OctetKeyPair ed25519(String keyId) {
        return _keyStorage.ed25519(keyId);
    }

    @Override
    public OctetKeyPair x25519(String keyId) {
        return _keyStorage.x25519(keyId);
    }

    @Override
    public ECKeyPair secp256k1(String keyId) {
        return _keyStorage.secp256k1(keyId);
    }
}
