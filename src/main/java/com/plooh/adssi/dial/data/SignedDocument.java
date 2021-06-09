package com.plooh.adssi.dial.data;

import java.util.List;

import lombok.Data;

/**
 * A SignedObject contains an arbitrary amount of data an a list of proofs
 * (signatures). Whereby proofs are not part of the signature. Everything else
 * is part of the signature.
 * 
 */
@Data
public class SignedDocument {
    private String id;
    private List<Proof> proof;
    private String type;
}