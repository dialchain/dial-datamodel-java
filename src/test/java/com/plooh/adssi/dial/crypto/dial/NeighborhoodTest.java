package com.plooh.adssi.dial.crypto.dial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.plooh.adssi.dial.ReadFileUtils;
import com.plooh.adssi.dial.crypto.CryptoService;
import com.plooh.adssi.dial.examples.utils.VerificationMethodUtils;
import com.plooh.adssi.dial.json.JSON;
import com.plooh.adssi.dial.parser.TimeFormat;
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
    public void distannceProperty() {
        final String n1 = "Francis";
        final String n2 = "Willie";
        final String n3 = "Shanel";
        final String n4 = "Filip";
        final String n5 = "Milica";
        Sha256Hash s1 = Sha256Hash.of(n1.getBytes(StandardCharsets.US_ASCII));
        Sha256Hash s2 = Sha256Hash.of(n2.getBytes(StandardCharsets.US_ASCII));
        Sha256Hash s3 = Sha256Hash.of(n3.getBytes(StandardCharsets.US_ASCII));
        Sha256Hash s4 = Sha256Hash.of(n4.getBytes(StandardCharsets.US_ASCII));
        Sha256Hash s5 = Sha256Hash.of(n5.getBytes(StandardCharsets.US_ASCII));
        Map<Double, String> distancesToN1 = Map.of(CryptoService.enp.distance(s1, s2), n2,
                CryptoService.enp.distance(s1, s3), n3, CryptoService.enp.distance(s1, s4), n4,
                CryptoService.enp.distance(s1, s5), n5);
        Map<Double, String> distancesToN2 = Map.of(CryptoService.enp.distance(s2, s1), n1,
                CryptoService.enp.distance(s2, s3), n3, CryptoService.enp.distance(s2, s4), n4,
                CryptoService.enp.distance(s2, s5), n5);

        Map<Double, String> distancesToN3 = Map.of(CryptoService.enp.distance(s3, s1), n1,
                CryptoService.enp.distance(s3, s2), n2, CryptoService.enp.distance(s3, s4), n4,
                CryptoService.enp.distance(s3, s5), n5);

        Map<Double, String> distancesToN4 = Map.of(CryptoService.enp.distance(s4, s1), n1,
                CryptoService.enp.distance(s4, s2), n2, CryptoService.enp.distance(s4, s3), n3,
                CryptoService.enp.distance(s4, s5), n5);
        Map<Double, String> distancesToN5 = Map.of(CryptoService.enp.distance(s5, s1), n1,
                CryptoService.enp.distance(s5, s2), n2, CryptoService.enp.distance(s5, s3), n3,
                CryptoService.enp.distance(s5, s4), n4);

        assertEquals(firstKey(distancesToN1), n3);
        assertEquals(firstKey(distancesToN2), n3);
        assertEquals(firstKey(distancesToN3), n2);
        assertEquals(firstKey(distancesToN4), n1);
        assertEquals(firstKey(distancesToN5), n3);
    }

    private String firstKey(Map<Double, String> m) {
        ArrayList<Double> arrayList = new ArrayList<>(m.keySet());
        Collections.sort(arrayList);
        return m.get(arrayList.get(0));
    }

    @Test
    public void neighBorhoodSimpleGroup1() {
        final int groupSize = 11;
        final int totalMembers = 81;
        final String anchor = UUID.randomUUID().toString();
        final List<String> members = new ArrayList<>();
        for (int i = 0; i < totalMembers; i++) {
            members.add(UUID.randomUUID().toString());
        }
        final NeiborhoodsAnchor partition1 = CryptoService.enp.partitionMembersToNeighbourhoods(anchor, members,
                groupSize);
        assertEquals(partition1.size(), totalMembers);
        List<Double> sortedAnchors = new ArrayList(partition1.getNeighborhoods().keySet());
        Collections.sort(sortedAnchors);
        assertEquals(partition1.getNeighborhoods().get(sortedAnchors.get(0)).size(), groupSize);
        // last set keep rest.
        assertEquals(partition1.getNeighborhoods().get(sortedAnchors.get((totalMembers / groupSize) - 1)).size(),
                groupSize + (totalMembers % groupSize));
        final NeiborhoodsAnchor partition2 = CryptoService.enp.partitionMembersToNeighbourhoods(anchor, members,
                groupSize);
        assertTrue(DataUtil.eq(partition1, partition2));

    }

    @Test
    public void neighBorhoodSimpleGroup2() {
        final int groupSize = 11;
        final int totalMembers = 81;
        final String anchor = UUID.randomUUID().toString();
        final List<String> members = new ArrayList<>();
        for (int i = 0; i < totalMembers; i++) {
            members.add(_keyId());
        }
        final NeiborhoodsAnchor partition1 = CryptoService.enp.partitionMembersToNeighbourhoods(anchor, members,
                groupSize);
        assertEquals(partition1.size(), totalMembers);
    }

    @Test
    public void neighBorhoodFromDartUuid81() throws IOException {
        String data = ReadFileUtils.readString("src/test/resources/dart-test-data/sample-population-81.json");
        NeiborhoodsAnchor na = JSON.MAPPER.readValue(data, NeiborhoodsAnchor.class);
        NeiborhoodsAnchor na2 = CryptoService.enp.partitionMembersToNeighbourhoods(na.getAnchor(),
                new ArrayList<>(na.getMembers()), 11);
        assertTrue(DataUtil.eq(na2.getNeighborhoods(), na.getNeighborhoods()));
    }

    @Test
    public void neighBorhoodFromDartPubkey81() throws IOException {
        String data = ReadFileUtils.readString("src/test/resources/dart-test-data/sample-population-81-pubkey.json");
        NeiborhoodsAnchor na = JSON.MAPPER.readValue(data, NeiborhoodsAnchor.class);
        NeiborhoodsAnchor na2 = CryptoService.enp.partitionMembersToNeighbourhoods(na.getAnchor(),
                new ArrayList<>(na.getMembers()), 11);
        assertTrue(DataUtil.eq(na2.getNeighborhoods(), na.getNeighborhoods()));
    }

    @Test
    public void neighBorhoodFromDartUuid813() throws IOException {
        String data = ReadFileUtils.readString("src/test/resources/dart-test-data/sample-population-813.json.dat");
        NeiborhoodsAnchor na = JSON.MAPPER.readValue(data, NeiborhoodsAnchor.class);
        NeiborhoodsAnchor na2 = CryptoService.enp.partitionMembersToNeighbourhoods(na.getAnchor(),
                new ArrayList<>(na.getMembers()), 11);
        assertTrue(DataUtil.eq(na2.getNeighborhoods(), na.getNeighborhoods()));
    }

    @Test
    public void neighBorhoodFromDartPubkey181303() throws IOException {
        String data = ReadFileUtils
                .readString("src/test/resources/dart-test-data/sample-population-181303-pubkey.json.dat");
        NeiborhoodsAnchor na = JSON.MAPPER.readValue(data, NeiborhoodsAnchor.class);
        NeiborhoodsAnchor na2 = CryptoService.enp.partitionMembersToNeighbourhoods(na.getAnchor(),
                new ArrayList<>(na.getMembers()), 11);
        assertTrue(DataUtil.eq(na2.getNeighborhoods(), na.getNeighborhoods()));
    }

    @Test
    public void neighBorhoodFromDartUuid181303() throws IOException {
        String data = ReadFileUtils.readString("src/test/resources/dart-test-data/sample-population-181303.json.dat");
        NeiborhoodsAnchor na = JSON.MAPPER.readValue(data, NeiborhoodsAnchor.class);
        NeiborhoodsAnchor na2 = CryptoService.enp.partitionMembersToNeighbourhoods(na.getAnchor(),
                new ArrayList<>(na.getMembers()), 11);
        assertTrue(DataUtil.eq(na2.getNeighborhoods(), na.getNeighborhoods()));
    }

    private String _keyId() {
        return VerificationMethodUtils.idKeyPairFromAssertionKey(TimeFormat.format(Instant.now()), 0, new HashMap<>());
    }
}
