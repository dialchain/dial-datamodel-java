package com.plooh.adssi.dial.crypto.dial;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.plooh.adssi.dial.util.DataUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NeiborhoodsAnchor {
    private String anchor;
    private Set<String> members = new HashSet<>();
    private Map<Double, Neighborhood> neighborhoods = new HashMap<>();

    int size() {
        int l = 0;
        Collection<Neighborhood> values = neighborhoods.values();
        for (Neighborhood e : values) {
            l += e.size();
        }
        return l;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NeiborhoodsAnchor other = (NeiborhoodsAnchor) obj;
        if (anchor == null) {
            if (other.anchor != null)
                return false;
        } else if (!anchor.equals(other.anchor))
            return false;
        if (members == null) {
            if (other.members != null)
                return false;
        } else if (!DataUtil.eq(members, other.members)) {
            return false;
        }
        if (neighborhoods == null) {
            if (other.neighborhoods != null)
                return false;
        } else if (!DataUtil.eq(neighborhoods, other.neighborhoods)) {
            return false;
        }
        return true;
    }
}
