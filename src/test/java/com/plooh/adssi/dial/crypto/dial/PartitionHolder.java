package com.plooh.adssi.dial.crypto.dial;

import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartitionHolder {
    private Set<String> anchors;
    private Set<String> members;
    private Map<String, Set<String>> partitions;
}