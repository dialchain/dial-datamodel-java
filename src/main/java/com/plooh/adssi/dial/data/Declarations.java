package com.plooh.adssi.dial.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Declarations extends SignedDocument {
    private List<Declaration> declaration = new ArrayList<>();
}