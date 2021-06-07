package com.plooh.adssi.dial.data;

import lombok.Data;

@Data
public class ReferencedDeclaration extends Declaration {
    public static final String TYPE = "Ref";
    private String cid;

    public ReferencedDeclaration() {
        super(TYPE);
    }

}