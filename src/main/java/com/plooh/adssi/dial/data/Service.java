package com.plooh.adssi.dial.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service {
    private String type;
    private String id;
    private String serviceEndpoint;
}