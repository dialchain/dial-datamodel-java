package com.plooh.adssi.dial.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class DIDeclaration extends Declaration {
    private String created;
    private List<String> controller = new ArrayList<>();
    private List<TreasuryAccount> account = new ArrayList<>();
    private List<VerificationMethod> verificationMethod = new ArrayList<>();
    private List<KeyAgreement> keyAgreement = new ArrayList<>();

    public DIDeclaration(String type) {
        super(type);
    }
}