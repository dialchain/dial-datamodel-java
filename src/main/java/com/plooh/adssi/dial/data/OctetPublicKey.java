package com.plooh.adssi.dial.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OctetPublicKey {
    private String kid;
    private String x;
    private String curve;
    private String keyUse;
    private String kty = "OKP";

}
