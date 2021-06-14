package com.plooh.adssi.dial.examples.participant;

import com.nimbusds.jose.jwk.JWK;
import com.plooh.adssi.dial.data.VerificationMethod;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerificationMethodData {
    private String id;
    private JWK keyPair;
    private VerificationMethod verificationMethod;
}