package com.plooh.adssi.dial.lookup;

import com.plooh.adssi.dial.data.Proof;
import com.plooh.adssi.dial.data.VerificationMethod;

public interface PublicKeyResolver {
    // Disccovers and returns the verification method associated with a proof.
    VerificationMethod lookup(Proof proof);
}
