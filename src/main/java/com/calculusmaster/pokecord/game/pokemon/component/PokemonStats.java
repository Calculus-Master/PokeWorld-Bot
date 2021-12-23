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
        this();

        if(values.length != Stat.values().length) throw new IllegalArgumentException("Input integer vararg must have %s elements!".formatted(Stat.values().length));

        Arrays.stream(Stat.values()).forEach(stat -> {
            this.map.put(stat, values[stat.ordinal()]);
            this.changes.put(stat, 0);
        });
    }

    public PokemonStats()
    {
        this.map = new LinkedHashMap<>();
        this.changes = new LinkedHashMap<>();

        Arrays.stream(Stat.values()).forEach(stat -> {
            this.map.put(stat, 0);
            this.changes.put(stat, 0);
        });
    }

    public PokemonStats(String condensed)
    {
        this();

        for(Stat s : Stat.values()) this.map.put(s, Integer.parseInt(condensed.split("-")[s.ordinal()]));
    }

    public String condense()
    {
        StringBuilder condensed = new StringBuilder();
        for(Stat s : Stat.values()) condensed.append(this.map.get(s)).append("-");
        return condensed.deleteCharAt(condensed.length() - 1).toString();
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
