package com.calculusmaster.pokecord.util.custom;

import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

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

    public static Map<StatusCondition, Boolean> createStatusConditionMap()
    {
        Map<StatusCondition, Boolean> map = new ExtendedHashMap<>();
        for(StatusCondition s : StatusCondition.values()) map.put(s, false);
        return map;
    }

    public ExtendedHashMap<K, V> editEach(BiFunction<? super K, ? super V, ? extends V> func)
    {
        this.replaceAll(func);
        return this;
    }
}
