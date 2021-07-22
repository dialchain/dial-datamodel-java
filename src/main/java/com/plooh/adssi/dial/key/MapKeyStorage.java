package com.plooh.adssi.dial.key;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.plooh.adssi.dial.data.ECKeyPair;
import com.plooh.adssi.dial.data.OctetKeyPair;

class MapKeyStorage implements KeyStorage {
    final String _uid;
    final Map<String, OctetKeyPair> _ed25519;
    final Map<String, OctetKeyPair> _x25519;
    final Map<String, ECKeyPair> _secp256k1;

    MapKeyStorage(String uid, Map<String, OctetKeyPair> ed25519, Map<String, OctetKeyPair> x25519,
            Map<String, ECKeyPair> secp256k1) {
        _uid = uid;
        _ed25519 = ed25519 != null ? ed25519 : new HashMap<>();
        _x25519 = x25519 != null ? x25519 : new HashMap<>();
        _secp256k1 = secp256k1 != null ? secp256k1 : new HashMap<>();
    }

    Map<String, OctetKeyPair> getOctetKeyPairs() {
        return new HashMap<>(_ed25519);

    }

    static Random rand = new Random();

    private static <T> T random(Map<String, T> map) {
        if (map.isEmpty())
            throw new IllegalStateException("Store has not keypair.");
        List<T> givenList = map.values().stream().collect(Collectors.toList());
        return givenList.get(rand.nextInt(givenList.size()));
    }

    @Override
    public String getUid() {
        return _uid;
    }

    @Override
    public OctetKeyPair ed25519(String keyId) {
        return _ed25519.get(keyId);
    }

    @Override
    public OctetKeyPair x25519(String keyId) {
        return _x25519.get(keyId);
    }

    @Override
    public ECKeyPair secp256k1(String keyId) {
        return _secp256k1.get(keyId);
    }

    @Override
    public OctetKeyPair randomEd25519() {
        return random(_ed25519);
    }

    @Override
    public OctetKeyPair randomX25519() {
        return random(_x25519);
    }

    @Override
    public ECKeyPair randomSecp256k1() {
        return random(_secp256k1);
    }
}
