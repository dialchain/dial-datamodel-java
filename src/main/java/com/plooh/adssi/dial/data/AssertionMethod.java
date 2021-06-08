package com.plooh.adssi.dial.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public abstract class AssertionMethod {
    private String type;
    private String id;

    public AssertionMethod(String type) {
        this.type = type;
    }

    public AssertionMethod(String type, String id) {
        this.type = type;
        this.id = id;
    }
}