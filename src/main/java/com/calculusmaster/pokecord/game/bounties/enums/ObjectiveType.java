package com.calculusmaster.pokecord.game.bounties.enums;

import com.calculusmaster.pokecord.game.bounties.objectives.*;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

import java.util.Random;
import java.util.function.Supplier;

public enum ObjectiveType
{
    DEFEAT_POKEMON(DefeatGenericObjective::new, 5, 50),
    DEFEAT_POKEMON_TYPE(DefeatTypeObjective::new, 5, 25),
    DEFEAT_POKEMON_POOL(DefeatPoolObjective::new, 5, 15),
    DEFEAT_LEGENDARY(DefeatLegendaryObjective::new, 1, 3),
    BUY_ITEMS(BuyItemsObjective::new, 1, 5),
    USE_MOVES(UseMoveGenericObjective::new, 20, 50),
    USE_MOVES_CATEGORY(UseMoveCategoryObjective::new, 10, 30),
    USE_MOVES_TYPE(UseMoveTypeObjective::new, 10, 30),
    USE_MOVES_NAME(UseMoveNameObjective::new, 2, 6),
    USE_MOVES_POOL(UseMovePoolObjective::new, 2, 10),
    USE_MOVES_POWER_LESS(UseMovePowerLessObjective::new, 5, 20),
    USE_MOVES_POWER_GREATER(UseMovePowerGreaterObjective::new, 5, 20),
    USE_MOVES_ACCURACY_LESS(UseMoveAccuracyLessObjective::new, 1, 10),
    USE_MOVES_PRIORITY_HIGH(UseMovePriorityHighObjective::new, 1, 5),
    USE_MOVES_PRIORITY_LOW(UseMovePriorityLowObjective::new, 1, 5),
    USE_ZMOVE(UseZMoveObjective::new, 2, 20),
    USE_ZMOVE_TYPE(UseZMoveTypeObjective::new, 2, 10),
    USE_MAX_MOVE(UseMaxMoveObjective::new, 10, 30),
    USE_MAX_MOVE_TYPE(UseMaxMoveTypeObjective::new, 10, 20),
    COMPLETE_TRADE(CompleteTradeObjective::new, 1, 4),
    COMPLETE_PVP_DUEL(CompletePVPDuelObjective::new, 1, 10),
    COMPLETE_WILD_DUEL(CompleteWildDuelObjective::new, 10, 80),
    COMPLETE_TRAINER_DUEL(WinTrainerDuelObjective::new, 2, 15),
    COMPLETE_ELITE_DUEL(CompleteEliteDuelObjective::new, 2, 10),
    COMPLETE_BOUNTY(CompleteBountyObjective::new, 1, 10),
    COMPLETE_GAUNTLET_LEVELS(CompleteGauntletLevelsObjective::new, 1, 10),
    WIN_PVP_DUEL(CompletePVPDuelObjective::new, 1, 5),
    WIN_WILD_DUEL(CompleteWildDuelObjective::new, 10, 40),
    WIN_TRAINER_DUEL(CompleteTrainerDuelObjective::new, 2, 10),
    WIN_ELITE_DUEL(CompleteEliteDuelObjective::new, 1, 5),
    WIN_RAID_DUEL(WinRaidObjective::new, 2, 4),
    CATCH_POKEMON(CatchGenericObjective::new, 30, 100),
    CATCH_POKEMON_TYPE(CatchTypeObjective::new, 20, 40),
    CATCH_POKEMON_NAME(CatchNameObjective::new, 1, 10),
    CATCH_POKEMON_POOL(CatchPoolObjective::new, 1, 15),
    EARN_XP_POKEMON(EarnPokemonXPObjective::new, 2000, 16000),
    EVOLVE_POKEMON(EvolvePokemonObjective::new, 1, 5),
    LEVEL_POKEMON(LevelPokemonObjective::new, 1, 20),
    EARN_EVS(EarnEVsGenericObjective::new, 10, 50),
    EARN_EVS_STAT(EarnEVsStatObjective::new, 10, 20),
    RELEASE_POKEMON(ReleaseGenericObjective::new, 1, 6),
    RELEASE_POKEMON_TYPE(ReleaseTypeObjective::new, 1, 3),
    RELEASE_POKEMON_NAME(ReleaseNameObjective::new, 1, 3),
    RELEASE_POKEMON_POOL(ReleasePoolObjective::new, 1, 4),
    SWAP_POKEMON(SwapGenericObjective::new, 5, 30),
    DAMAGE_POKEMON(DamageGenericObjective::new, 100, 3000),
    DAMAGE_POKEMON_TYPE(DamageTypeObjective::new, 100, 1500),
    DAMAGE_POKEMON_CATEGORY(DamageCategoryObjective::new, 100, 2500),
    PARTICIPATE_RAID(ParticipateRaidObjective::new, 2, 5),
    BREED_POKEMON(BreedGenericObjective::new, 1, 10);

    public Supplier<? extends Objective> constructor;
    private final int min;
    private final int max;

    ObjectiveType(Supplier<? extends Objective> constructor, int min, int max)
    {
        this.constructor = constructor;
        this.min = min;
        this.max = max;
    }

    public int getRandomTarget()
    {
        return new Random().nextInt(this.max - this.min + 1) + this.min;
    }

    public static ObjectiveType cast(String objectiveType)
    {
        for(ObjectiveType o : values()) if(o.toString().equals(objectiveType.toUpperCase())) return o;
        return null;
    }
}
