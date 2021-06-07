package com.plooh.adssi.dial.data;

import lombok.Data;

@Data
public class SignatureAssertionMethod extends AssertionMethod {
    public static final String TYPE = "Signature";
    private String verificationMethod;

    public SignatureAssertionMethod(String id) {
        super(TYPE, id);
    }
}