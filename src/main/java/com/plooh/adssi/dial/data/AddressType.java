package com.plooh.adssi.dial.data;

import java.nio.charset.StandardCharsets;

import io.ipfs.multibase.Multibase;

public enum AddressType {
    phone, email, iban, dns, uuid;

    public final String normalize(String seed) {
        String urlString = name() + ":" + seed == null ? "" : seed.toLowerCase();
        return Multibase.encode(Multibase.Base.Base64, urlString.getBytes(StandardCharsets.UTF_8));
    }
}