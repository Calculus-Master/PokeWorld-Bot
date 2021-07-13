package com.calculusmaster.pokecord.game.bounties.enums;

import com.calculusmaster.pokecord.game.bounties.objectives.*;

import java.util.function.Supplier;

public enum ObjectiveType
{
    DEFEAT_POKEMON(DefeatGenericObjective::new),
    DEFEAT_POKEMON_TYPE(DefeatTypeObjective::new),
    DEFEAT_LEGENDARY(DefeatLegendaryObjective::new),
    BUY_ITEMS(BuyItemsObjective::new),
    USE_ZMOVE(UseZMoveObjective::new),
    USE_MAX_MOVE(UseMaxMoveObjective::new),
    COMPLETE_TRADE(CompleteTradeObjective::new),
    COMPLETE_PVP_DUEL(CompletePVPDuelObjective::new),
    COMPLETE_WILD_DUEL(CompleteWildDuelObjective::new),
    COMPLETE_ELITE_DUEL(CompleteEliteDuelObjective::new),
    WIN_PVP_DUEL(CompletePVPDuelObjective::new),
    WIN_WILD_DUEL(CompleteWildDuelObjective::new),
    WIN_ELITE_DUEL(CompleteEliteDuelObjective::new),
    CATCH_POKEMON(CatchGenericObjective::new),
    CATCH_POKEMON_TYPE(CatchTypeObjective::new),
    CATCH_POKEMON_NAME(CatchNameObjective::new),
    EARN_XP_POKEPASS(EarnPokePassXPObjective::new),
    EARN_XP_POKEMON(EarnPokemonXPObjective::new),
    EVOLVE_POKEMON(EvolvePokemonObjective::new),
    LEVEL_POKEMON(LevelPokemonObjective::new),
    EARN_EVS(EarnEVsGenericObjective::new),
    EARN_EVS_STAT(EarnEVsStatObjective::new);

    public Supplier<? extends Objective> constructor;
    ObjectiveType(Supplier<? extends Objective> constructor) { this.constructor = constructor; }

    public static ObjectiveType cast(String objectiveType)
    {
        for(ObjectiveType o : values()) if(o.toString().equals(objectiveType.toUpperCase())) return o;
        return null;
    }
}
