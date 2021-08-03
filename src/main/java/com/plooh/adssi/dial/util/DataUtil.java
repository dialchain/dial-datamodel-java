package com.plooh.adssi.dial.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DataUtil {

    public static <T> boolean isNullOrEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
    }

    public static boolean eq(Object a, Object b) {
        if (a == b || a.equals(b))
            return true;
        if (a instanceof List && b instanceof List)
            return _listEquals((List) a, (List) b);
        if (a instanceof Map && b instanceof Map)
            return _mapEquals((Map) a, (Map) b);
        if (a instanceof Set && b instanceof Set)
            return _setEquals((Set) a, (Set) b);
        return false;
    }

    private static boolean _setEquals(Set a, Set b) {
        // for a set we expect elt to have a clean == operator
        return a.size() == b.size() && a.containsAll(b);
    }

    private static boolean _listEquals(List a, List b) {
        if (a.size() != b.size())
            return false;
        for (var index = 0; index < a.size(); index++) {
            // For a list we assume element can be collections
            if (!eq(a.get(index), b.get(index)))
                return false;
        }
        return true;
    }

    private static boolean _mapEquals(Map a, Map b) {
        if (a.size() != b.size())
            return false;
        for (Object key : a.keySet()) {
            // for a map we expect keys to have a proper == operator.
            if (!b.containsKey(key) || !eq(b.get(key), a.get(key))) {
                return false;
            }
        }
        return true;
    }
}
