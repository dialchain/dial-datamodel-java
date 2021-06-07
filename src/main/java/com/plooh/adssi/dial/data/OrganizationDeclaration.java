package com.plooh.adssi.dial.data;

import java.util.List;

public class OrganizationDeclaration extends DIDeclaration {
    public static final String TYPE = "Organization";

    private List<VerificationMethod> verificationMethod;
    private List<SignatureAssertionMethod> assertionMethod;

    public OrganizationDeclaration() {
        super(TYPE);
    }

}