package com.calculusmaster.pokecord.game.pokemon.component;

import com.calculusmaster.pokecord.game.enums.elements.Stat;
import org.bson.Document;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class PokemonStats
{
    private final LinkedHashMap<Stat, Integer> map;

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

    public Document serialized()
    {
        Document doc = new Document();
        Arrays.stream(Stat.values()).forEach(stat -> doc.append(stat.toString(), this.map.get(stat)));
        return doc;
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
