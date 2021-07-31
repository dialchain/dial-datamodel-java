package com.plooh.adssi.dial.crypto.dial;

import java.util.Collection;
import java.util.HashMap;
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
public class Neighborhood {
    /// THe south boundary of the neiborhood. For the first neighborhood, this
    /// will be double 0.
    private Double boundarySouth;

    /// The north boundary of this neighborhood. For the last neighborhood, this
    /// will
    /// be double.maxValue.
    private Double boundaryNorth;

    /// Neighbors ordered by their distance to the anchor.
    private Map<Double, Set<String>> neighbors = new HashMap<>();

    int size() {
        int l = 0;
        Collection<Set<String>> values = neighbors.values();
        for (Set<String> e : values) {
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
        Neighborhood other = (Neighborhood) obj;
        if (boundaryNorth == null) {
            if (other.boundaryNorth != null)
                return false;
        } else if (!boundaryNorth.equals(other.boundaryNorth))
            return false;
        if (boundarySouth == null) {
            if (other.boundarySouth != null)
                return false;
        } else if (!boundarySouth.equals(other.boundarySouth))
            return false;
        if (neighbors == null) {
            if (other.neighbors != null)
                return false;
        } else if (!DataUtil.eq(neighbors, other.neighbors)) {
            return false;
        }
        return true;
    }

}
