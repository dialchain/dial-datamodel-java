package com.plooh.adssi.dial.data;

import java.util.List;

import lombok.Data;

@Data
public class VoteAssertionMethod extends AssertionMethod {
    public static final String TYPE = "Vote";
    private int quorum;
    private List<OrganizationMember> member;

    public VoteAssertionMethod(String id) {
        super(TYPE, id);
    }
}