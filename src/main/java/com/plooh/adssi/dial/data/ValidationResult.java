package com.plooh.adssi.dial.data;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class ValidationResult {
    final String key;
    final Map<String, Object> details = new HashMap<>();

    public ValidationResult(String key, Map<String, Object> details) {
        this.key = key;
        this.details.putAll(details);
    }
}
