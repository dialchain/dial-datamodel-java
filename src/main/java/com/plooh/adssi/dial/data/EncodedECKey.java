package com.plooh.adssi.dial.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncodedECKey {
    private byte[] bytes;
    private EncodedECPublicKey publicKey;
}
