package com.calculusmaster.pokecord.game.bounties;

public enum ObjectiveType
{
    DEFEAT_POKEMON,
    DEFEAT_POKEMON_TYPE;

    public static ObjectiveType cast(String objectiveType)
    {
        for(ObjectiveType o : values()) if(o.toString().equals(objectiveType.toUpperCase())) return o;
        return null;
    }
}
