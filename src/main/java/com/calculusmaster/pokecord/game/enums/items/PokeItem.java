package com.calculusmaster.pokecord.game.enums.items;

import com.calculusmaster.pokecord.util.Global;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum PokeItem
{
    NONE,
    THUNDER_STONE;

    public String getName()
    {
        return Global.normalCase(this.toString());
    }

    public static PokeItem asItem(String s)
    {
        return Arrays.stream(values()).filter(i -> i.toString().equals(s.toUpperCase())).collect(Collectors.toList()).get(0);
    }

    public static boolean isItem(String s)
    {
        return Arrays.stream(values()).anyMatch(i -> i.toString().equals(s.toUpperCase()));
    }
}
