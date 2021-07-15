package com.plooh.adssi.dial.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OctetKeyPair {
    private OctetPublicKey publicKey;
    private String d;
}
