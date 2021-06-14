package com.plooh.adssi.dial.data;

import java.util.List;

import lombok.Data;

@Data
public class Proof {
    private String document;
    private String issuer;
    private String created;
    private String proofPurpose;
    private String type;
    private List<String> assertionMethod;
    private String verificationMethod;
    private String signatureValue;
    private String nonce;
}