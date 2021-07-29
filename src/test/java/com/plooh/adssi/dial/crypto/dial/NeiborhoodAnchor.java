package com.plooh.adssi.dial.crypto.dial;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NeiborhoodAnchor {
    private String anchor;
    private Map<Double, String> neighbors;
}