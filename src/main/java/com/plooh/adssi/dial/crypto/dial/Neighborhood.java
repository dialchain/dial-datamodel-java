package com.plooh.adssi.dial.crypto.dial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.bitcoinj.core.Sha256Hash;

//final enp = Neighbourhood._();

/// Efemeral neighbourhood protocol.
/// Use an anchor to distribute validators (members) to neighbourhoods
/// Drop each declarationn in a deterministically derived neighbourhood for this time window.
/// Neighbour will care for the double spending check.
public class Neighborhood {
    public static final Neighborhood enp = new Neighborhood();

    private Neighborhood() {
    }

    /// - partitions a population to anchors, using the euclidean distannce to
    /// ccompute
    /// the proximity och each member to an anchor.
    /// - uses the lexicographical ordering to define priority among anchors.
    public Map<Sha256Hash, List<Sha256Hash>> partitionMembersToNeighbourhoods(List<Sha256Hash> anchors,
            List<Sha256Hash> members) {
        // order neighbourhood anchors lexicographically
        anchors.sort(new Comparator<Sha256Hash>() {
            @Override
            public int compare(Sha256Hash o1, Sha256Hash o2) {
                return o1.compareTo(o2);
            }

        });

        // result map
        final Map<Sha256Hash, List<Sha256Hash>> neigborhoods = new HashMap<>();

        // Compute group size, first groups will keep the overflow
        final int groupSize = members.size() / anchors.size();
        var restToShare = members.size() % anchors.size();

        final List<Sha256Hash> remainingMembers = new ArrayList<Sha256Hash>(members);
        for (var i = 0; i < anchors.size(); i++) {
            final Sha256Hash anchor = anchors.get(i);
            // Map of members ordered by distance to this anchor
            TreeMap<Double, Sha256Hash> distances = distancesOrdered(anchor, remainingMembers);
            // new TreeMap<Double, Sha256Hash>();
            // remainingMembers.stream().forEach(member -> distances.put(distance(anchor,
            // member), member));

            final List<Sha256Hash> closestNeighbours = new ArrayList<Sha256Hash>();

            // Take the first k members closest to anchor
            // drop them from list.
            for (var k = 0; k < groupSize; k++) {
                Entry<Double, Sha256Hash> entry = distances.pollFirstEntry();
                closestNeighbours.add(entry.getValue());
                remainingMembers.remove(entry.getValue());
            }

            // Share the overflow to the firsts in line.
            if (restToShare > 0) {
                restToShare--;
                Entry<Double, Sha256Hash> entry = distances.pollFirstEntry();
                closestNeighbours.add(entry.getValue());
                remainingMembers.remove(entry.getValue());
            }

            neigborhoods.put(anchor, closestNeighbours);
        }

        return neigborhoods;
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
