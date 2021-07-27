package com.plooh.adssi.dial.data;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class Declaration {
    private String id;
    private String type;

    public Declaration(String type) {
        this.type = type;
    }

}