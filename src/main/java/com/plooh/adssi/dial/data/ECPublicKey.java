package com.plooh.adssi.dial.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ECPublicKey {
    private String kid;
    private String w;
    private String curve;
    private String keyUse;
    private String kty = "EC";
}
