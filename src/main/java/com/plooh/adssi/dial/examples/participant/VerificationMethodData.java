package com.plooh.adssi.dial.examples.participant;

import com.plooh.adssi.dial.data.EncodedECKey;
import com.plooh.adssi.dial.data.VerificationMethod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationMethodData {
    private String id;
    private EncodedECKey keyPair;
    private VerificationMethod verificationMethod;
}