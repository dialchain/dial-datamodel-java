package com.plooh.adssi.dial.data;

import lombok.Data;

@Data
public class KeyAgreement {
    private String type;
    private String id;
    private String publicKeyMultibase;
}