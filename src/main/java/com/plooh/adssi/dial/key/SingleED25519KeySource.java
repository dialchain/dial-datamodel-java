package com.plooh.adssi.dial.key;

import com.plooh.adssi.dial.data.ECKeyPair;
import com.plooh.adssi.dial.data.OctetKeyPair;

public class SingleED25519KeySource implements KeySource {
    final OctetKeyPair _okp;

    public SingleED25519KeySource(OctetKeyPair okp) {
        if ("sig".equals(okp.getPublicKey().getKeyUse())) {
            throw new IllegalArgumentException("Supports only ED25519 keys.");
        }
        this._okp = okp;
    }

    @Override
    public OctetKeyPair ed25519(String keyId) {
        if (_okp.getPublicKey().getKid() != keyId) {
            throw new IllegalArgumentException("Single entry source expect only keyid:" + _okp.getPublicKey().getKid());
        }
        return _okp;
    }

    @Override
    public OctetKeyPair x25519(String keyId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ECKeyPair secp256k1(String keyId) {
        throw new UnsupportedOperationException();
    }
}