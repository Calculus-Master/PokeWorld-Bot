package com.calculusmaster.pokecord.game.enums.elements;

import com.calculusmaster.pokecord.util.Global;

import java.util.Arrays;

public enum Stat
{
    HP("HP"), ATK("Attack"), DEF("Defense"), SPATK("Special Attack"), SPDEF("Special Defense"), SPD("Speed");

    public String name;
    Stat(String fullName) {this.name = fullName;}

    public int index()
    {
        return Arrays.asList(values()).indexOf(this);
    }

    public static Stat cast(String stat)
    {
        return (Stat) Global.getEnumFromString(values(), stat);
    }
}
