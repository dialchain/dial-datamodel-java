package com.plooh.adssi.dial.lookup;

import java.util.Collections;

import com.plooh.adssi.dial.data.VerificationMethod;

public class SinglePublicKeyResolver extends MapBackedPublicKeyResolver {

    public SinglePublicKeyResolver(VerificationMethod vm) {
        super(Collections.singletonMap(vm.getId(), vm));
    }

}