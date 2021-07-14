package com.plooh.adssi.dial.ipfs;

import static org.assertj.core.api.Assertions.*;

import com.plooh.adssi.dial.cid.CidUtils;
import io.ipfs.cid.Cid;
import io.ipfs.multibase.Multibase;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class CidTest {

    @Test
    void shouldComputeCidB32Success() throws IOException {
        // HUMAN READABLE CID
        // base32 - cidv1 - raw - (sha2-256 : 256 : 1B4F3A24B07692A1EE25382B2F181A2E6AF787686684DB0C798CE424F8DAE6E1)
        var expected = "bafkreia3j45cjmdwskq64jjyfmxrqgronl3yo2dgqtnqy6mm4qsprwxg4e";

        String input = readString("./src/test/resources/participant.json");
        var actual = CidUtils.jcsCidB32(input);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldComputeCidB58Success() throws IOException {
        // HUMAN READABLE CID
        // base58btc - cidv1 - raw - (sha2-256 : 256 : 1B4F3A24B07692A1EE25382B2F181A2E6AF787686684DB0C798CE424F8DAE6E1)
        var expected = "zb2rhYUtGcPvAFkVJzbPEpK5aRht6TU3tyJiAKgjC2zEXUj9r";
        String input = readString("./src/test/resources/participant.json");

        var actual = CidUtils.jcsCidB58(input);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldComputeMultihashFromCidB58Success() throws IOException {
        var input = "zb2rhYUtGcPvAFkVJzbPEpK5aRht6TU3tyJiAKgjC2zEXUj9r";

        Cid cid = Cid.decode(input);
        var actual = Multibase.encode(Multibase.Base.Base58BTC, cid.toBytes());

        assertThat(actual).isEqualTo(input);
    }

    private String readString(String path) throws IOException {
        return Files.readString(Paths.get(path));
    }

}