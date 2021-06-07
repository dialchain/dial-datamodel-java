package com.plooh.adssi.dial.data;

import java.util.List;

import lombok.Data;

@Data
public class Proof {
    private String declaration;
    private String issuer;
    private String created;
    private String type;
    private List<String> assertionMethod;
    private String signatureValue;
    private String nonce;
}