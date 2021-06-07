package com.plooh.adssi.dial.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Declarations {
    private String id;
    private List<Declaration> entries = new ArrayList<>();
}