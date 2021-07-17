package com.plooh.adssi.dial.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OctetPublicKey {
    private String kid;
    private String x;
    private String curve;
    private String keyUse;
    private String kty = "OKP";

}
