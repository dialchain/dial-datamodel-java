package com.plooh.adssi.dial.data;

import lombok.Data;

@Data
public class Publication extends SignedDocument {
    private Object document;
    private Twindow twindow;
}