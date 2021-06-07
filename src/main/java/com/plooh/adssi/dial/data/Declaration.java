package com.plooh.adssi.dial.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public abstract class Declaration {
    private String type;

    public Declaration(String type) {
        this.type = type;
    }

}