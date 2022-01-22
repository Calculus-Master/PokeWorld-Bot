package com.calculusmaster.pokecord.game.pokemon.component;

import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.util.Global;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class PokemonDuelStatChanges
{
    private LinkedHashMap<Stat, Integer> changes;
    private int accuracy;
    private int evasion;

    public PokemonDuelStatChanges()
    {
        this.changes = new LinkedHashMap<>();
        Arrays.stream(Stat.values()).forEach(s -> this.changes.put(s, 0));
        this.accuracy = 0;
        this.evasion = 0;
    }

    private void checkInvalidChange(Stat s)
    {
        if(this.changes.get(s) < -6) this.changes.put(s, -6);

        if(this.changes.get(s) > 6) this.changes.put(s, 6);
    }

    public int get(Stat s)
    {
        return this.changes.get(s);
    }

    public void set(Map<Stat, Integer> map)
    {
        this.changes = new LinkedHashMap<>(Map.copyOf(map));
    }

    public Map<Stat, Integer> getAll()
    {
        return this.changes;
    }

    public void change(Stat s, int change)
    {
        this.changes.put(s, this.changes.get(s) + change);

        this.checkInvalidChange(s);
    }

    public void clear(Stat s)
    {
        this.changes.put(s, 0);
    }

    public void clear()
    {
        this.changes.clear();
        Arrays.stream(Stat.values()).forEach(s -> this.changes.put(s, 0));
        this.accuracy = 0;
        this.evasion = 0;
    }

    public void changeEvasion(int change)
    {
        this.evasion = Global.clamp(this.evasion + change, -6, 6);
    }

    public void changeAccuracy(int change)
    {
        this.accuracy = Global.clamp(this.accuracy + change, -6, 6);
    }

    public double getModifier(Stat s)
    {
        return this.changes.get(s) == 0 ? 1.0 : (double) (this.changes.get(s) < 0 ? 2 : 2 + Math.abs(this.changes.get(s))) / (this.changes.get(s) < 0 ? 2 + Math.abs(this.changes.get(s)) : 2);
    }

    public int getEvasion()
    {
        return this.evasion;
    }

    public int getAccuracy()
    {
        return this.accuracy;
    }
}
