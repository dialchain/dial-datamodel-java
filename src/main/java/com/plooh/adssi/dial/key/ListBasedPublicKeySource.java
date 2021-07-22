package com.plooh.adssi.dial.key;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.plooh.adssi.dial.data.ECPublicKey;
import com.plooh.adssi.dial.data.OctetPublicKey;

public class ListBasedPublicKeySource implements PublicKeySource {
    final Map<String, OctetPublicKey> _ed25519;
    final Map<String, OctetPublicKey> _x25519;
    final Map<String, ECPublicKey> _secp256k1;

    ListBasedPublicKeySource(List<OctetPublicKey> ed25519Keys, List<OctetPublicKey> x25519Keys,
            List<ECPublicKey> secp256k1Keys) {
        _ed25519 = listToMap(ed25519Keys);
        _x25519 = listToMap(x25519Keys);
        _secp256k1 = listToMap2(secp256k1Keys);

        ed25519Keys.forEach(okp -> {
            if (!okp.getKeyUse().equals("sig")) {
                throw new IllegalArgumentException("Key with keyId " + okp.getKid()
                        + "in ed25519Keys contains key with wrong key use " + okp.getKeyUse());
            }
        });

        x25519Keys.forEach(okp -> {
            if (!okp.getKeyUse().equals("enc")) {
                throw new IllegalArgumentException("Key with keyId " + okp.getKid()
                        + "in ex5519Keys contains key with wrong key use " + okp.getKeyUse());
            }
        });
    }

    private static Map<String, OctetPublicKey> listToMap(List<OctetPublicKey> list) {
        Map<String, OctetPublicKey> map = new HashMap<>();
        for (OctetPublicKey okp : list) {
            map.put(okp.getKid(), okp);
        }
        return map;
    }

    private static Map<String, ECPublicKey> listToMap2(List<ECPublicKey> list) {
        Map<String, ECPublicKey> map = new HashMap<>();
        for (ECPublicKey eckp : list) {
            map.put(eckp.getKid(), eckp);
        }
        return map;
    }

    static ListBasedPublicKeySource fromOkps(List<OctetPublicKey> okps) {
        final List<OctetPublicKey> ed25519Keys = okps.stream().filter(okp -> okp.getKeyUse().equals("sig"))
                .collect(Collectors.toList());
        final List<OctetPublicKey> x25519Keys = okps.stream().filter(okp -> okp.getKeyUse().equals("enc"))
                .collect(Collectors.toList());
        final List<ECPublicKey> secp256k1Keys = new ArrayList<>();
        return new ListBasedPublicKeySource(ed25519Keys, x25519Keys, secp256k1Keys);
    }

    @Override
    public OctetPublicKey ed25519(String keyId) {
        return _ed25519.get(keyId);
    }

    @Override
    public OctetPublicKey x25519(String keyId) {
        return _x25519.get(keyId);
    }

    @Override
    public ECPublicKey secp256k1(String keyId) {
        return _secp256k1.get(keyId);
    }
}