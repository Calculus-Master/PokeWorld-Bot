package com.calculusmaster.pokecord.game.settings.value;

public class DoubleValue<V1, V2> implements SettingValue
{
    private V1 value1;
    private V2 value2;

    public DoubleValue(V1 value1, V2 value2)
    {
        this.value1 = value1;
        this.value2 = value2;
    }

    public V1 get1()
    {
        return this.value1;
    }

    public V2 get2()
    {
        return this.value2;
    }

    public void set1(V1 value1)
    {
        this.value1 = value1;
    }

    public void set2(V2 value2)
    {
        this.value2 = value2;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null) return false;
        else if(obj == this) return true;
        else if(!(obj instanceof DoubleValue<?, ?> other)) return false;
        else return this.value1.equals(other.value1) && this.value2.equals(other.value2);
    }
}
