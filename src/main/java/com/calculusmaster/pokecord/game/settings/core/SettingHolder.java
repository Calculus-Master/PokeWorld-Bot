package com.calculusmaster.pokecord.game.settings.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class SettingHolder<V extends SettingValue>
{
    protected final String key;
    protected final Map<String, V> settingMap;
    protected final Supplier<V> defaultValue;
    protected final Function<String, V> valueReader;
    protected final Function<V, String> valueSerializer;

    public SettingHolder(String key, Supplier<V> defaultValue, Function<String, V> valueReader, Function<V, String> valueSerializer)
    {
        this.key = key;
        this.settingMap = Collections.synchronizedMap(new HashMap<>());
        this.defaultValue = defaultValue;
        this.valueReader = valueReader;
        this.valueSerializer = valueSerializer;
    }

    public V getDefault()
    {
        return this.defaultValue.get();
    }

    protected String getKey()
    {
        return "settings." + this.key;
    }
}
