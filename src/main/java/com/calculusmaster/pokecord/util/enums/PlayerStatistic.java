package com.calculusmaster.pokecord.util.enums;

public enum PlayerStatistic
{
    POKEMON_CAUGHT("pokemon_caught"),
    PVP_DUELS_WON("pvp_duels_won"),
    WILD_DUELS_WON("wild_duels_won"),
    TRAINER_DUELS_WON("trainer_duels_won"),
    ELITE_TRAINER_DUELS_WON("elite_trainer_duels_won"),
    CREDITS_SPENT("credits_spent"),
    CREDITS_EARNED("credits_earned"),
    NATURAL_REDEEMS_EARNED("redeems_earned"),
    POKEMON_SOLD_MARKET("sold_market"),
    POKEMON_BOUGHT_MARKET("bought_market"),
    TRADES_COMPLETED("trades_completed"),
    SHOP_ITEMS_BOUGHT("shop_items_bought"),
    //TODO: Not Implemented
    BOUNTIES_COMPLETED("bounties_completed"),
    PVP_DUELS_COMPLETED("pvp_duels_completed"),
    WILD_DUELS_COMPLETED("wild_duels_completed"),
    TRAINER_DUELS_COMPLETED("trainer_duels_completed"),
    ELITE_TRAINER_DUELS_COMPLETED("elite_trainer_duels_completed"),
    SHOP_TMS_BOUGHT("shop_tms_bought"),
    SHOP_TRS_BOUGHT("shop_trs_bought"),
    GAUNTLETS_WON("gauntlets_won"),
    GAUNTLETS_COMPLETED("gauntlets_completed"),
    RAIDS_WON("raids_won"),
    RAIDS_WON_MVP("raids_won_mvp"),
    RAIDS_COMPLETED("raids_completed"),
    POKEMON_BRED("pokemon_bred"),
    PURSUITS_COMPLETED("pursuits_completed"),
    PURSUITS_COMPLETED_MINI("pursuits_completed_mini"),
    PURSUITS_COMPLETED_SHORT("pursuits_completed_short"),
    PURSUITS_COMPLETED_AVERAGE("pursuits_completed_average"),
    PURSUITS_COMPLETED_LONG("pursuits_completed_long"),
    PURSUITS_COMPLETED_JOURNEY("pursuits_completed_journey"),
    PURSUITS_COMPLETED_LEGEND("pursuits_completed_legend"),
    POKEMON_DEFEATED("pokemon_defeated"),
    POKEMON_FAINTED("pokemon_fainted"),
    COMMANDS_USED("commands_used"),
    MOVES_LEARNED("moves_learned"),
    MOVES_USED("moves_used"),
    ZMOVES_USED("zmoves_used"),
    MAX_MOVES_USED("max_moves_used"),
    DUELS_COMPLETED_MAX_SIZE("duels_completed_max"),
    EGGS_HATCHED("eggs_hatched"),
    POKEMON_EXP_EARNED("pokemon_exp_earned"),
    POKEPASS_EXP_EARNED("pokepass_exp_earned"),
    MASTERY_EXP_EARNED("mastery_exp_earned"),
    MESSAGES_SENT("messages_sent"),
    POKEMON_EVOLVED("pokemon_evolved");

    public String key;
    PlayerStatistic(String key)
    {
        this.key = key;
    }
}
