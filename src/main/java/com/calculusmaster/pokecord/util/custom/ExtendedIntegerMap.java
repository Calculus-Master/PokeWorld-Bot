package com.calculusmaster.pokecord.util.custom;

import java.util.Arrays;
import java.util.Collection;

public class ExtendedIntegerMap<K> extends ExtendedHashMap<K, Integer>
{
    public ExtendedIntegerMap()
    {
        super();
    }

    public ExtendedHashMap<K, Integer> increase(K key)
    {
        this.put(key, this.get(key) + 1);
        return this;
    }

    public ExtendedHashMap<K, Integer> reset()
    {
        this.forEach((k, v) -> this.put(k, 0));
        return this;
    }

    @SafeVarargs
    public final ExtendedIntegerMap<K> withDefaultKeys(K... keys)
    {
        return this.withDefaultKeys(Arrays.asList(keys));
    }

    public final ExtendedIntegerMap<K> withDefaultKeys(Collection<? extends K> keys)
    {
        for(K k : keys) this.insert(k, 0);
        return this;
    }
}
