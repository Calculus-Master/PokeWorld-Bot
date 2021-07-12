package com.calculusmaster.pokecord.game.bounties.enums;

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
    WIN_ELITE_DUEL,
    CATCH_POKEMON,
    CATCH_POKEMON_TYPE,
    CATCH_POKEMON_NAME,
    EARN_XP_POKEPASS,
    EARN_XP_POKEMON,
    EVOLVE_POKEMON,
    LEVEL_POKEMON;

    public static ObjectiveType cast(String objectiveType)
    {
        for(ObjectiveType o : values()) if(o.toString().equals(objectiveType.toUpperCase())) return o;
        return null;
    }
}