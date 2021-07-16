package com.calculusmaster.pokecord.util.custom;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ExtendedHashMap<K, V> extends HashMap<K, V>
{
    public ExtendedHashMap(Map<? extends K, ? extends V> map)
    {
        super(map);
    }

    public ExtendedHashMap()
    {
        super();
    }

    public ExtendedHashMap<K, V> insert(K key, V value)
    {
        this.put(key, value);
        return this;
    }

    public Map<K, V> asMap()
    {
        return this;
    }

    public Optional<Map<K, V>> optional()
    {
        return Optional.of(this);
    }

    public static <K, V> ExtendedHashMap<K, V> copy(Map<K, V> map)
    {
        return new ExtendedHashMap<>(Map.copyOf(map));
    }
}
