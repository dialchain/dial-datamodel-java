package com.plooh.adssi.dial.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignatureResult {
    final String signedRecord;
    final List<ValidationResult> issues;

}
