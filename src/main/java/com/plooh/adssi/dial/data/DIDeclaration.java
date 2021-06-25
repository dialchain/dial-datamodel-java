package com.plooh.adssi.dial.data;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class DIDeclaration extends Declaration {
    private String id;
    private String created;
    private List<String> controller;
    private List<TreasuryAccount> account;
    private List<VerificationMethod> verificationMethod;

    public DIDeclaration(String type) {
        super(type);
    }
}