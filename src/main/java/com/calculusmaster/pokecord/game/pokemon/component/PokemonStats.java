package com.calculusmaster.pokecord.game.pokemon.component;

import com.calculusmaster.pokecord.game.enums.elements.Stat;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class PokemonStats
{
    private final LinkedHashMap<Stat, Integer> map;
    private final LinkedHashMap<Stat, Integer> changes;

    public PokemonStats(int... values)
    {
        if(values.length != Stat.values().length) throw new IllegalArgumentException("Input integer vararg must have %s elements!".formatted(Stat.values().length));

        this.map = new LinkedHashMap<>();
        Arrays.stream(Stat.values()).forEach(stat -> this.map.put(stat, values[stat.ordinal()]));

        this.changes = new LinkedHashMap<>();
        Arrays.stream(Stat.values()).forEach(stat -> this.changes.put(stat, 0));
    }

    public LinkedHashMap<Stat, Integer> get()
    {
        return this.map;
    }

    public LinkedHashMap<Stat, Integer> getChanges()
    {
        return this.changes;
    }
}
