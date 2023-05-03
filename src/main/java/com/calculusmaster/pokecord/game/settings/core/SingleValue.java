package com.calculusmaster.pokecord.game.settings.core;

public class SingleValue<V> implements SettingValue
{
    private V value;

    public SingleValue(V value)
    {
        this.value = value;
    }

    public V get()
    {
        return this.value;
    }

    public void set(V value)
    {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null) return false;
        else if(obj == this) return true;
        else if(!(obj instanceof SingleValue<?> other)) return false;
        else return this.value.equals(other.value);
    }
}