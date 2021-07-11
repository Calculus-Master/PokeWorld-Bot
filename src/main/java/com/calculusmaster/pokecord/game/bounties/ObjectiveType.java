package com.calculusmaster.pokecord.game.bounties;

public enum ObjectiveType
{
    DEFEAT_POKEMON,
    DEFEAT_POKEMON_TYPE,
    DEFEAT_LEGENDARY,
    BUY_ITEMS,
    USE_ZMOVE,
    USE_MAX_MOVE,
    COMPLETE_TRADE,
    COMPLETE_PVP_DUEL,
    COMPLETE_WILD_DUEL,
    COMPLETE_ELITE_DUEL,
    WIN_PVP_DUEL,
    WIN_WILD_DUEL,
    WIN_ELITE_DUEL;

    public static ObjectiveType cast(String objectiveType)
    {
        for(ObjectiveType o : values()) if(o.toString().equals(objectiveType.toUpperCase())) return o;
        return null;
    }
}
