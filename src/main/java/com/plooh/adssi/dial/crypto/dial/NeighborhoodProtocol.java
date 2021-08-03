package com.plooh.adssi.dial.crypto.dial;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.TreeMap;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.bitcoinj.core.Sha256Hash;

//final enp = Neighbourhood._();

/// Efemeral neighbourhood protocol.
/// Use an anchor to distribute validators (members) to neighbourhoods
/// Drop each declarationn in a deterministically derived neighbourhood for this time window.
/// Neighbour will care for the double spending check.
public class NeighborhoodProtocol {
    public static final NeighborhoodProtocol enp = new NeighborhoodProtocol();

    private NeighborhoodProtocol() {
    }

    /// - Spread the population arround the anchor, using the euclidean distance of
    /// each member to the anchor
    /// - Uses the anchor as salt to hash each member (hash(anchor+member)) and
    /// therefore spoil members predictable proximity.
    /// - Group members into neighborhoods of minGroupSize neighbors
    /// - Overflow member assigned to the first last neighborhood.
    /// - Key for each neighborhood is the hash of tthe closest member.
    /// As distances can colide, there is no predicctable size of neigborhood. All
    /// what we know is that each neighborhood
    /// hast min of minGroupSize.
    public NeiborhoodsAnchor partitionMembersToNeighbourhoods(String anchorStr, List<String> members,
            int minGroupSize) {
        final Sha256Hash anchor = Sha256Hash.of(anchorStr.getBytes(StandardCharsets.UTF_8));
        final Map<Sha256Hash, String> membersMap = new HashMap<>();
        members.forEach(m -> membersMap.put(Sha256Hash.of(m.getBytes(StandardCharsets.UTF_8)), m));

        // Use anchor to hash each member
        final Map<Double, List<Sha256Hash>> membersOrderedByDistance = new HashMap<>();
        membersMap.entrySet().forEach(m -> {
            byte[] concatArray = new byte[anchor.getBytes().length + m.getKey().getBytes().length];
            System.arraycopy(anchor.getBytes(), 0, concatArray, 0, anchor.getBytes().length);
            System.arraycopy(m.getKey().getBytes(), 0, concatArray, anchor.getBytes().length,
                    m.getKey().getBytes().length);
            final Double d = distance(anchor, Sha256Hash.of(concatArray));
            if (membersOrderedByDistance.get(d) == null) {
                membersOrderedByDistance.put(d, new ArrayList<Sha256Hash>());
            }
            membersOrderedByDistance.get(d).add(m.getKey());
        });

        // result map
        final Map<Double, Neighborhood> neighborhoods = new HashMap<>();
        final List<Double> keyList = new ArrayList<Double>(membersOrderedByDistance.keySet());
        keyList.sort(new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return o1.compareTo(o2);
            }
        });

        Neighborhood currentNeighborhood = null;
        Neighborhood lastNeighborhood = null;
        for (var i = 0; i < keyList.size(); i++) {
            final Double distance = keyList.get(i);
            if (currentNeighborhood == null) {
                currentNeighborhood = new Neighborhood();
            }
            if (currentNeighborhood.getBoundarySouth() == null) {
                currentNeighborhood.setBoundarySouth(distance);
            }
            neighborhoods.put(currentNeighborhood.getBoundarySouth(), currentNeighborhood);

            currentNeighborhood.getNeighbors().put(distance, membersOrderedByDistance.get(distance).stream()
                    .map(e -> membersMap.get(e)).collect(Collectors.toSet()));
            if (currentNeighborhood.size() >= minGroupSize) {
                if (lastNeighborhood != null) {
                    lastNeighborhood.setBoundaryNorth(currentNeighborhood.getBoundarySouth());
                } else {
                    // Set the first south boundary to null;
                    currentNeighborhood.setBoundarySouth(null);
                }
                lastNeighborhood = currentNeighborhood;
                currentNeighborhood = null;
            }
        }

        // If the last group is less than group size, merge with the one before last.
        if (currentNeighborhood != null && currentNeighborhood.size() < minGroupSize) {
            lastNeighborhood.getNeighbors().putAll(currentNeighborhood.getNeighbors());
            lastNeighborhood.setBoundaryNorth(null);
            neighborhoods.remove(currentNeighborhood.getBoundarySouth());
        }
        Set<String> membersSet = members.stream().collect(Collectors.toSet());
        return NeiborhoodsAnchor.builder().neighborhoods(neighborhoods).anchor(anchorStr).members(membersSet).build();
    }

    public TreeMap<Double, Sha256Hash> distancesOrdered(Sha256Hash anchor, List<Sha256Hash> members) {
        // Map of members ordered by distance to this anchor
        TreeMap<Double, Sha256Hash> distances = new TreeMap<Double, Sha256Hash>();
        members.stream().forEach(member -> distances.put(distance(anchor, member), member));
        return distances;
    }

    /// Drops a declaration into the closest neighbourhood for validation.
    Sha256Hash dropDeclarationInNeigbourhood(List<Sha256Hash> anchors, Sha256Hash declaration) {
        // OrderedMap will use the natural order of the keys.
        TreeMap<Double, Sha256Hash> distances = new TreeMap<Double, Sha256Hash>();
        anchors.stream().forEach(anchor -> distances.put(distance(anchor, declaration), anchor));

        // fisrt etry is the closest neighbour
        return distances.firstEntry().getValue();
    }

    double distance(Sha256Hash s1, Sha256Hash s2) {
        EuclideanDistance euc = new EuclideanDistance();
        return euc.compute(_fromSha256(s1), _fromSha256(s2));
    }

    double[] _fromSha256(Sha256Hash elt) {
        byte[] eltBytes = elt.getBytes();
        double[] result = new double[eltBytes.length];
        for (int j = 0; j < eltBytes.length; j++) {
            // convert to signed bytes before cast to double.
            result[j] = eltBytes[j] & 0xff;
        }
        return result;
    }
}
