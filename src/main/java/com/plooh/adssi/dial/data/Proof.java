package com.plooh.adssi.dial.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Proof {
    private String issuer;
    private String created;
    private String proofPurpose;
    private String type;
    private List<String> assertionMethod;
    private String verificationMethod;
    private String signatureValue;
    private String nonce;
}