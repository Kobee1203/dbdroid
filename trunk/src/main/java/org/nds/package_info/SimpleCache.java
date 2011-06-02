package org.nds.package_info;

import java.util.HashMap;
import java.util.Map;

abstract class SimpleCache<K, V> {
    private final Map<K, V> map = new HashMap<K, V>();

    protected abstract V load(K key);

    final V get(K key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        V value = load(key);
        map.put(key, value);
        return value;
    }
}
