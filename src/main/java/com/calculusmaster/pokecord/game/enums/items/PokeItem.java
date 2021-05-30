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
    DAWN_STONE(250),
    SHINY_STONE(250),
    OVAL_STONE(250),
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
    DUBIOUS_DISC(250),
    PRISM_SCALE(250),
    REAPER_CLOTH(250),
    DEEP_SEA_TOOTH(250),
    DEEP_SEA_SCALE(250),
    PROTECTOR(250),
    ELECTIRIZER(250),
    MAGMARIZER(250),
    SACHET(250),
    WHIPPED_DREAM(250),
    DRACO_PLATE(1000),
    DREAD_PLATE(1000),
    EARTH_PLATE(1000),
    FIST_PLATE(1000),
    FLAME_PLATE(1000),
    ICICLE_PLATE(1000),
    INSECT_PLATE(1000),
    IRON_PLATE(1000),
    MEADOW_PLATE(1000),
    MIND_PLATE(1000),
    PIXIE_PLATE(1000),
    SKY_PLATE(1000),
    SPLASH_PLATE(1000),
    SPOOKY_PLATE(1000),
    STONE_PLATE(1000),
    TOXIC_PLATE(1000),
    ZAP_PLATE(1000);

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
