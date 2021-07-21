package com.plooh.adssi.dial.util;

import java.util.Collection;

public final class DataUtil {

    public static <T> boolean isNullOrEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
    }

}
