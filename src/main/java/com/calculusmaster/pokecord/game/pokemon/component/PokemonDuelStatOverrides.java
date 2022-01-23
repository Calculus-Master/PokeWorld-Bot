package com.calculusmaster.pokecord.game.pokemon.component;

import com.calculusmaster.pokecord.game.enums.elements.Stat;

import java.util.HashMap;
import java.util.Map;

public class PokemonDuelStatOverrides
{
    private Map<Stat, Integer> overrides;

    public PokemonDuelStatOverrides()
    {
        this.overrides = new HashMap<>();
    }

    public void set(Stat s, int value)
    {
        this.overrides.put(s, value);
    }

    public void remove(Stat s)
    {
        this.overrides.remove(s);
    }

    public int get(Stat s)
    {
        return this.overrides.getOrDefault(s, 0);
    }

    public boolean has(Stat s)
    {
        return this.get(s) == 0;
    }
}
