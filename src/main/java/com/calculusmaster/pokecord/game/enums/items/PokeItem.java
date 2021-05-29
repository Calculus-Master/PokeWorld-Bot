package com.calculusmaster.pokecord.game.enums.items;

import com.calculusmaster.pokecord.util.Global;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum PokeItem
{
    NONE(0),
    THUNDER_STONE(250),
    ICE_STONE(250),
    MOON_STONE(250),
    FIRE_STONE(250),
    LEAF_STONE(250),
    SUN_STONE(250),
    WATER_STONE(250),
    DUSK_STONE(250),
    TRADE_EVOLVER(1000),
    METAL_COAT(1500),
    ZYGARDE_CUBE(6000),
    KINGS_ROCK(250),
    GALARICA_CUFF(250),
    GALARICA_WREATH(250),
    RAZOR_FANG(250),
    RAZOR_CLAW(250),
    DRAGON_SCALE(250),
    UPGRADE(250),
    DUBIOUS_DISC(250);

    public int cost;
    PokeItem(int cost)
    {
        this.cost = cost;
    }

    public String getName()
    {
        return Global.normalCase(this.toString());
    }

    public String getStyledName()
    {
        return Global.normalCase(this.getName().replaceAll("_", " "));
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
