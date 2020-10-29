package com.calculusmaster.pokecord.game.enums;

import com.calculusmaster.pokecord.util.Global;

import java.util.Arrays;

public enum Stat
{
    HP, ATK, DEF, SPATK, SPDEF, SPD;

    public int index()
    {
        return Arrays.asList(values()).indexOf(this);
    }

    public static Stat cast(String stat)
    {
        return (Stat) Global.getEnumFromString(values(), stat);
    }
}
