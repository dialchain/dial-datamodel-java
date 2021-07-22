package com.plooh.adssi.dial.key;

import com.plooh.adssi.dial.data.ECKeyPair;
import com.plooh.adssi.dial.data.OctetKeyPair;

public class SingleX25519KeySource implements KeySource {
    final OctetKeyPair _okp;

    public SingleX25519KeySource(OctetKeyPair okp) {
        if (!"enc".equals(okp.getPublicKey().getKeyUse())) {
            throw new IllegalArgumentException("Supports only X25519 keys.");
        }
        this._okp = okp;
    }

    @Override
    public OctetKeyPair ed25519(String keyId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OctetKeyPair x25519(String keyId) {
        if (!_okp.getPublicKey().getKid().equals(keyId)) {
            throw new IllegalArgumentException("Single entry source expect only keyid:" + _okp.getPublicKey().getKid());
        }
        return _okp;
    }

    @Override
    public ECKeyPair secp256k1(String keyId) {
        throw new UnsupportedOperationException();
    }

}