package com.plooh.adssi.dial.data;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrganizationDeclaration extends DIDeclaration {
    public static final String TYPE = "Organization";

    private List<VoteAssertionMethod> assertionMethod;

    public OrganizationDeclaration() {
        super(TYPE);
    }
}