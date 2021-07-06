package com.plooh.adssi.dial.data;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Publication extends SignedDocument {
    private List<String> cid;
    private String a_hash;
}