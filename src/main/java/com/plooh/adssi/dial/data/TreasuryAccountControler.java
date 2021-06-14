package com.plooh.adssi.dial.data;

import java.util.List;

import lombok.Data;

@Data
public class TreasuryAccountControler {
    private int quorum;
    private List<String> verificationMethod;
}