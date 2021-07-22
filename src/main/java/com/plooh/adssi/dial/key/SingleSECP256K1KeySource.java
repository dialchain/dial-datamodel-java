package com.plooh.adssi.dial.key;

import com.plooh.adssi.dial.data.ECKeyPair;
import com.plooh.adssi.dial.data.OctetKeyPair;

public class SingleSECP256K1KeySource implements KeySource {
    final ECKeyPair _eckp;

    public SingleSECP256K1KeySource(ECKeyPair eckp) {
        this._eckp = eckp;
    }

    @Override
    public OctetKeyPair ed25519(String keyId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OctetKeyPair x25519(String keyId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ECKeyPair secp256k1(String keyId) {
        if (!_eckp.getPublicKey().getKid().equals(keyId)) {
            throw new IllegalArgumentException(
                    "Single entry source expect only keyId:" + _eckp.getPublicKey().getKid());
        }
        return _eckp;
    }

}