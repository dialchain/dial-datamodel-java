package com.plooh.adssi.dial.data;

import lombok.Data;

@Data
public class VerificationMethod {
    private String type;
    private String id;
    private String publicKeyMultibase;
}