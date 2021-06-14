
package com.plooh.adssi.dial.data;

import lombok.Data;

@Data
public class TreasuryDeclaration extends DIDeclaration {
    public static final String TYPE = "Treasury";

    public TreasuryDeclaration() {
        super(TYPE);
    }
}