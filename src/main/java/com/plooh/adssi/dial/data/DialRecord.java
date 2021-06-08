package com.plooh.adssi.dial.data;

import java.util.List;

import lombok.Data;

@Data
public class DialRecord {
    private Declarations declaration;
    private List<Proof> proof;
    private Twindow twindow;
    private List<Proof> publication;
}