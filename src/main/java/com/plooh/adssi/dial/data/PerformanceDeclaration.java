package com.plooh.adssi.dial.data;

import java.util.List;

import lombok.Data;

@Data
public class PerformanceDeclaration extends ParticipantDeclaration {
    public static final String TYPE = "Perfomance";

    private List<Service> service;

    public PerformanceDeclaration() {
        super(TYPE);
    }

    public PerformanceDeclaration(List<Service> service) {
        super(TYPE);
        this.service = service;
    }

}