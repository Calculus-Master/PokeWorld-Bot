package com.calculusmaster.pokecord.game.pokemon.component;

import com.calculusmaster.pokecord.game.enums.elements.Stat;
import org.bson.Document;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class PokemonStats
{
    private final LinkedHashMap<Stat, Integer> map;

    public PokemonStats(int... values)
    {
        this();

        if(values.length != Stat.values().length) throw new IllegalArgumentException("Input integer vararg must have %s elements!".formatted(Stat.values().length));

        Arrays.stream(Stat.values()).forEach(stat -> this.map.put(stat, values[stat.ordinal()]));
    }

    public PokemonStats()
    {
        this.map = new LinkedHashMap<>();

        Arrays.stream(Stat.values()).forEach(stat -> this.map.put(stat, 0));
    }

    public PokemonStats(Document data)
    {
        this();

        Arrays.stream(Stat.values()).forEach(s -> this.set(s, data.getInteger(s.toString())));
    }

    public PokemonStats(String condensed)
    {
        this();

        for(Stat s : Stat.values()) this.map.put(s, Integer.parseInt(condensed.split("-")[s.ordinal()]));
    }

    public Document serialized()
    {
        Document doc = new Document();
        Arrays.stream(Stat.values()).forEach(stat -> doc.append(stat.toString(), this.map.get(stat)));
        return doc;
    }

    public String condense()
    {
        StringBuilder condensed = new StringBuilder();
        for(Stat s : Stat.values()) condensed.append(this.map.get(s)).append("-");
        return condensed.deleteCharAt(condensed.length() - 1).toString();
    }

    public int get(Stat s)
    {
        return this.map.get(s);
    }

    public void set(Stat s, int value)
    {
        this.map.put(s, value);
    }

    public void increase(Stat s, int amount)
    {
        this.set(s, this.map.get(s) + amount);
    }

    public void decrease(Stat s, int amount)
    {
        this.set(s, this.map.get(s) - amount);
    }

    public LinkedHashMap<Stat, Integer> get()
    {
        return this.map;
    }
}
