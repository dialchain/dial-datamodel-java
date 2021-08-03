package com.plooh.adssi.dial.examples.participant;

import com.plooh.adssi.dial.data.EncodedECKey;
import com.plooh.adssi.dial.data.KeyAgreement;
import com.plooh.adssi.dial.data.SignatureAssertionMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeyAgreementData {
    private String id;
    private EncodedECKey keyPair;
    private KeyAgreement keyAgreement;
    private SignatureAssertionMethod assertionMethod;
}