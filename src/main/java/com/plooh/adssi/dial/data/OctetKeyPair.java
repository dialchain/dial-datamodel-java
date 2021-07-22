package com.plooh.adssi.dial.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OctetKeyPair {
    private OctetPublicKey publicKey;
    private String d;
}
