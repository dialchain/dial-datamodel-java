package com.plooh.adssi.dial.key;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.plooh.adssi.dial.data.ECKeyPair;
import com.plooh.adssi.dial.data.OctetKeyPair;

public class ListBasedKeySource extends StorageKeySource {

    ListBasedKeySource(List<OctetKeyPair> ed25519Keys, List<OctetKeyPair> x25519Keys, List<ECKeyPair> secp256k1Keys) {
        super(new MapKeyStorage("uid", listToMap(ed25519Keys), listToMap(x25519Keys), listToMap2(secp256k1Keys)));

        ed25519Keys.forEach(okp -> {
            if (!okp.getPublicKey().getKeyUse().equals("sig")) {
                throw new IllegalArgumentException("Key with keyId " + okp.getPublicKey().getKid()
                        + "in ed25519Keys contains key with wrong key use " + okp.getPublicKey().getKeyUse());
            }
        });

        x25519Keys.forEach(okp -> {
            if (!okp.getPublicKey().getKeyUse().equals("enc")) {
                throw new IllegalArgumentException("Key with keyId " + okp.getPublicKey().getKid()
                        + "in ex5519Keys contains key with wrong key use " + okp.getPublicKey().getKeyUse());
            }
        });
    }

    private static Map<String, OctetKeyPair> listToMap(List<OctetKeyPair> list) {
        Map<String, OctetKeyPair> map = new HashMap<>();
        for (OctetKeyPair okp : list) {
            map.put(okp.getPublicKey().getKid(), okp);
        }
        return map;
    }

    private static Map<String, ECKeyPair> listToMap2(List<ECKeyPair> list) {
        Map<String, ECKeyPair> map = new HashMap<>();
        for (ECKeyPair eckp : list) {
            map.put(eckp.getPublicKey().getKid(), eckp);
        }
        return map;
    }

    static ListBasedKeySource fromOkps(List<OctetKeyPair> okps) {
        final List<OctetKeyPair> ed25519Keys = okps.stream().filter(okp -> okp.getPublicKey().getKeyUse().equals("sig"))
                .collect(Collectors.toList());
        final List<OctetKeyPair> x25519Keys = okps.stream().filter(okp -> okp.getPublicKey().getKeyUse().equals("enc"))
                .collect(Collectors.toList());
        final List<ECKeyPair> secp256k1Keys = new ArrayList<>();
        return new ListBasedKeySource(ed25519Keys, x25519Keys, secp256k1Keys);
    }
}