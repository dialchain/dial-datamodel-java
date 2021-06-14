package com.plooh.adssi.dial.data;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class DIDeclaration extends Declaration {
    private String id;
    private String created;
    private List<String> controller;
    private List<TreasuryAccount> account;

    public DIDeclaration(String type) {
        super(type);
    }
}