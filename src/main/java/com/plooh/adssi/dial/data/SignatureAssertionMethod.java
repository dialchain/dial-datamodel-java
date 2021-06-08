package com.plooh.adssi.dial.data;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignatureAssertionMethod extends AssertionMethod {
    public static final String TYPE = "Signature";
    private String verificationMethod;

    public SignatureAssertionMethod(String id) {
        super(TYPE, id);
    }
}