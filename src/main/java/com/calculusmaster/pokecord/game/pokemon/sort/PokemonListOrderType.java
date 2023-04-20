package com.calculusmaster.pokecord.game.pokemon.sort;

import com.calculusmaster.pokecord.util.Global;

import java.util.Arrays;

public enum PokemonListOrderType
{
    NUMBER("Number"),
    IV("Total IV"),
    EV("Total EV"),
    STAT("Total Stat"),
    LEVEL("Level"),
    NAME("Name"),
    RANDOM("Random"),
    PRICE("Price"),
    TIME("Time")
    ;

    private final String name;

    PokemonListOrderType(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public static PokemonListOrderType cast(String input)
    {
        PokemonListOrderType o = Global.getEnumFromString(values(), input);
        if(o == null) o = Arrays.stream(values()).filter(ot -> ot.getName().equalsIgnoreCase(input)).findFirst().orElse(null);
        return o;
    }
}
