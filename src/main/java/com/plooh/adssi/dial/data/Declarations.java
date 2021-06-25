package com.plooh.adssi.dial.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Declarations extends SignedDocument {
    private List<Declaration> declaration = new ArrayList<>();
}