package com.plooh.adssi.dial.data;

import lombok.Data;

@Data
public class TreasuryAccount {
    private String network;
    private String address;
    private TreasuryAccountControler control;
}