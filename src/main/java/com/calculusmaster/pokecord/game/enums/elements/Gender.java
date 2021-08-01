package com.calculusmaster.pokecord.game.enums.elements;

public enum Gender
{
    MALE,
    FEMALE,
    UNKNOWN;

    public static Gender cast(String s)
    {
        for(Gender g : values()) if(g.toString().equalsIgnoreCase(s)) return g;
        return null;
    }
}
