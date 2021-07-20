package com.plooh.adssi.dial;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReadFileUtils {
    public static String readString(String path) throws IOException {
        return Files.readString(Paths.get(path));
    }
}