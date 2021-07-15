package com.plooh.adssi.dial.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EncodedECPublicKey {
    private byte[] bytes;
    private byte[] hash;
    private boolean compressed;
}
