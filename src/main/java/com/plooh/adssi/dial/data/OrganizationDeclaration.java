package com.plooh.adssi.dial.data;

import java.util.List;

import lombok.Data;

@Data
public class OrganizationDeclaration extends DIDeclaration {
    public static final String TYPE = "Organization";

    private List<VoteAssertionMethod> assertionMethod;

    private List<Service> service;

    public OrganizationDeclaration() {
        super(TYPE);
    }

}