package com.plooh.adssi.dial.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ECPublicKey {
    private String kid;
    private String w;
    private String curve;
    private String keyUse;
    private String kty = "EC";
}
