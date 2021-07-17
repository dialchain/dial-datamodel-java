package com.plooh.adssi.dial.lookup;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.data.VerificationMethod;

public class MapBackedPublicKeyResolver implements PublicKeyResolver {
    final Map<String, VerificationMethod> _store;

    public Map<String, VerificationMethod> getStore() {
        return Collections.unmodifiableMap(_store);
    }

    MapBackedPublicKeyResolver(Map<String, VerificationMethod> store) {
        this._store = store != null ? store : new HashMap<>();
    }

    @Override
    public VerificationMethod lookup(Proof proof) {
        return _store.get(proof.getVerificationMethod());
    }
}
