package com.calculusmaster.pokecord.util.enums;

public enum PlayerStatistic
{
    /* Planning
        - pokemon caught
        - pvp duels won
        - wild duels won
        - trainer duels won
        - elite trainer duels won
        - total credits earned
        - total credits spent
        - natural redeems earned
        - pokemon sold on market
        - pokemon bought from market
        - trades done
        - number of items bought from shop
     */

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
    BOUNTIES_COMPLETED("bounties_completed");

    public String key;
    PlayerStatistic(String key)
    {
        this.key = key;
    }
}
