package com.plooh.adssi.dial.data;

import lombok.Data;

@Data
public class Service {
    private String type;
    private String id;
    private String serviceEndpoint;
    private String assertionMethod;
}