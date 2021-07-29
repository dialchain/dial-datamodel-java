package com.plooh.adssi.dial.crypto.dial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.plooh.adssi.dial.ReadFileUtils;
import com.plooh.adssi.dial.crypto.CryptoService;
import com.plooh.adssi.dial.json.JSON;
import com.plooh.adssi.dial.util.DataUtil;

import org.bitcoinj.core.Sha256Hash;
import org.junit.jupiter.api.Test;

public class NeighborhoodTest {

    @Test
    public void euclideanSha256() {
        var s1 = Sha256Hash.of("Francis".getBytes(StandardCharsets.US_ASCII));
        var s2 = Sha256Hash.of("Willie".getBytes(StandardCharsets.US_ASCII));
        var sd_12 = CryptoService.enp.distance(s1, s2);
        var s3 = Sha256Hash.of("Shanel".getBytes(StandardCharsets.US_ASCII));
        var sd_13 = CryptoService.enp.distance(s1, s3);
        var sd_23 = CryptoService.enp.distance(s2, s3);
        var sd_21 = CryptoService.enp.distance(s2, s1);
        var sd_31 = CryptoService.enp.distance(s3, s1);
        var sd_32 = CryptoService.enp.distance(s3, s2);

        var sd_11 = CryptoService.enp.distance(s1, s1);
        var sd_22 = CryptoService.enp.distance(s2, s2);
        var sd_33 = CryptoService.enp.distance(s3, s3);

        // Distance to self
        assertEquals(sd_11, 0);
        assertEquals(sd_22, 0);
        assertEquals(sd_33, 0);

        // distance
        assertEquals(sd_12, sd_21);
        assertEquals(sd_13, sd_31);
        assertEquals(sd_23, sd_32);
    }

    @Test
    public void neighBorhoodSimpleGroup1() {
        final int groupSize = 11;
        final List<Sha256Hash> anchors = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            anchors.add(Sha256Hash.of(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8)));
        }
        final List<Sha256Hash> members = new ArrayList<>();
        for (int i = 0; i < 81; i++) {
            members.add(Sha256Hash.of(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8)));
        }
        Map<Sha256Hash, List<Sha256Hash>> partition1 = CryptoService.enp.partitionMembersToNeighbourhoods(anchors,
                members);
        var sortedAnchors = partition1.keySet().stream().sorted().collect(Collectors.toList());

        assertEquals(partition1.get(sortedAnchors.get(0)).size(), groupSize + 1);
        assertEquals(partition1.get(sortedAnchors.get(3)).size(), groupSize + 1);
        assertEquals(partition1.get(sortedAnchors.get(4)).size(), groupSize);
        assertEquals(partition1.get(sortedAnchors.get(6)).size(), groupSize);

        // final partition2 =
        // CryptoService.enp.partitionMembersToNeighbourhoods(anchors, members);
        Map<Sha256Hash, List<Sha256Hash>> partition2 = CryptoService.enp.partitionMembersToNeighbourhoods(anchors,
                members);
        assertEquals(partition1, partition2);
        assertTrue(partition1.equals(partition2));

        assertEquals(partition1.size(), 7);
        assertTrue(partition1.get(sortedAnchors.get(0)) != partition1.get(sortedAnchors.get(1)));

        assertEquals(intersection(setFrom(partition1.get(sortedAnchors.get(0))),
                setFrom(partition1.get(sortedAnchors.get(0)))), setFrom(partition1.get(sortedAnchors.get(0))));

        assertTrue(intersection(setFrom(partition1.get(sortedAnchors.get(0))),
                setFrom(partition1.get(sortedAnchors.get(1)))).isEmpty());
        assertTrue(intersection(setFrom(partition1.get(sortedAnchors.get(0))),
                setFrom(partition1.get(sortedAnchors.get(2)))).isEmpty());
        assertTrue(intersection(setFrom(partition1.get(sortedAnchors.get(0))),
                setFrom(partition1.get(sortedAnchors.get(3)))).isEmpty());
        assertTrue(intersection(setFrom(partition1.get(sortedAnchors.get(0))),
                setFrom(partition1.get(sortedAnchors.get(4)))).isEmpty());
        assertTrue(intersection(setFrom(partition1.get(sortedAnchors.get(0))),
                setFrom(partition1.get(sortedAnchors.get(5)))).isEmpty());
        assertTrue(intersection(setFrom(partition1.get(sortedAnchors.get(0))),
                setFrom(partition1.get(sortedAnchors.get(6)))).isEmpty());

        assertTrue(intersection(setFrom(partition1.get(sortedAnchors.get(1))),
                setFrom(partition1.get(sortedAnchors.get(2)))).isEmpty());
        assertTrue(intersection(setFrom(partition1.get(sortedAnchors.get(1))),
                setFrom(partition1.get(sortedAnchors.get(3)))).isEmpty());
        assertTrue(intersection(setFrom(partition1.get(sortedAnchors.get(1))),
                setFrom(partition1.get(sortedAnchors.get(4)))).isEmpty());
        assertTrue(intersection(setFrom(partition1.get(sortedAnchors.get(1))),
                setFrom(partition1.get(sortedAnchors.get(5)))).isEmpty());
        assertTrue(intersection(setFrom(partition1.get(sortedAnchors.get(1))),
                setFrom(partition1.get(sortedAnchors.get(6)))).isEmpty());

        assertTrue(intersection(setFrom(partition1.get(sortedAnchors.get(6))),
                setFrom(partition1.get(sortedAnchors.get(0)))).isEmpty());
        assertTrue(intersection(setFrom(partition1.get(sortedAnchors.get(6))),
                setFrom(partition1.get(sortedAnchors.get(1)))).isEmpty());
        assertTrue(intersection(setFrom(partition1.get(sortedAnchors.get(6))),
                setFrom(partition1.get(sortedAnchors.get(2)))).isEmpty());
        assertTrue(intersection(setFrom(partition1.get(sortedAnchors.get(6))),
                setFrom(partition1.get(sortedAnchors.get(3)))).isEmpty());
        assertTrue(intersection(setFrom(partition1.get(sortedAnchors.get(6))),
                setFrom(partition1.get(sortedAnchors.get(4)))).isEmpty());
        assertTrue(intersection(setFrom(partition1.get(sortedAnchors.get(6))),
                setFrom(partition1.get(sortedAnchors.get(5)))).isEmpty());

    }

    @Test
    public void neighBorhoodFromDart() throws IOException {
        String data = ReadFileUtils.readString(
                "src/test/java/com/plooh/adssi/dial/crypto/dial/dart-test-data/sample-population-7-81.json");
        PartitionHolder partitionHolder = JSON.MAPPER.readValue(data, PartitionHolder.class);

        final Map<Sha256Hash, String> anchorsMap = partitionHolder.getAnchors().stream()
                .collect(Collectors.toMap(a -> _sha256Hash(a), Function.identity()));
        final Map<Sha256Hash, String> membersMap = partitionHolder.getMembers().stream()
                .collect(Collectors.toMap(a -> _sha256Hash(a), Function.identity()));

        final List<Sha256Hash> anchors = new ArrayList<Sha256Hash>(anchorsMap.keySet());
        final List<Sha256Hash> members = new ArrayList<Sha256Hash>(membersMap.keySet());

        final Map<Sha256Hash, List<Sha256Hash>> partitions = CryptoService.enp.partitionMembersToNeighbourhoods(anchors,
                members);
        final Map<String, Set<String>> partitionStr = new HashMap<>();
        partitions.entrySet().stream()
                .forEach(e -> partitionStr.put(anchorsMap.get(e.getKey()), _map(e.getValue(), membersMap)));

        assertTrue(DataUtil.eq(partitionHolder.getPartitions().keySet(), partitionStr.keySet()));

        assertTrue(DataUtil.eq(partitionHolder.getPartitions(), partitionStr));
    }

    @Test
    public void neighBorhoodAnchors() throws IOException {
        String data = ReadFileUtils.readString(
                "src/test/java/com/plooh/adssi/dial/crypto/dial/dart-test-data/sample-population-7-81.json");
        PartitionHolder partitionHolder = JSON.MAPPER.readValue(data, PartitionHolder.class);

        final Map<Sha256Hash, String> anchorsMap = partitionHolder.getAnchors().stream()
                .collect(Collectors.toMap(a -> _sha256Hash(a), Function.identity()));
        final Map<Sha256Hash, String> membersMap = partitionHolder.getMembers().stream()
                .collect(Collectors.toMap(a -> _sha256Hash(a), Function.identity()));

        final List<Sha256Hash> members = new ArrayList<Sha256Hash>(membersMap.keySet());

        String anchrosData = ReadFileUtils.readString(
                "src/test/java/com/plooh/adssi/dial/crypto/dial/dart-test-data/anchor-ed9f7a30-c23f-47e8-b9b0-c77b204756ba.json");
        NeiborhoodAnchor neiborhoodAnchor = JSON.MAPPER.readValue(anchrosData, NeiborhoodAnchor.class);
        final Sha256Hash anchor = anchorsMap.entrySet().stream()
                .filter(e -> e.getValue().equals(neiborhoodAnchor.getAnchor())).findFirst().get().getKey();

        final TreeMap<Double, Sha256Hash> distancesOrdered = CryptoService.enp.distancesOrdered(anchor, members);
        final Map<Double, String> resultMap = new TreeMap<>();
        distancesOrdered.entrySet().stream().forEach(e -> resultMap.put(e.getKey(), membersMap.get(e.getValue())));
        TreeMap<Double, String> neigborsOrdered = new TreeMap<>(neiborhoodAnchor.getNeighbors());
        assertTrue(DataUtil.eq(resultMap, neigborsOrdered));
    }

    private static Sha256Hash _sha256Hash(String a) {
        return Sha256Hash.of(a.getBytes(StandardCharsets.UTF_8));
    }

    private static Set<Sha256Hash> setFrom(List<Sha256Hash> list) {
        return new HashSet<>(list);
    }

    private static Set<Sha256Hash> intersection(Set<Sha256Hash> s1, Set<Sha256Hash> s2) {
        s1.retainAll(s2);
        return s1;
    }

    private Set<String> _map(List<Sha256Hash> input, Map<Sha256Hash, String> extent) {
        final Set<String> result = new HashSet<String>();
        input.forEach(e -> {
            if (extent.containsKey(e))
                result.add(extent.get(e));
        });
        return result;
    }

}
