package com.calculusmaster.pokecord.game.player.leaderboard;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.StatisticType;

import java.util.function.Function;

public enum LeaderboardScoreComponent
{
    POKEDEX_REGISTERED("Pokemon Registered", 0.75F, p -> p.getPokedex().getSize()),
    MASTERY_LEVEL("Pokemon Mastery Level", 0.5F, PlayerDataQuery::getLevel),
    ACHIEVEMENTS("Achievements", 0.8F, p -> p.getAchievements().size()),
    CREDITS_EARNED("Credits Earned", 0.05F, p -> p.getStatistics().get(StatisticType.CREDITS_EARNED)),
    FORMS_OWNED("Forms Collected", 0.4F, p -> p.getOwnedForms().size()),
    MEGAS_OWNED("Mega-Evolutions Collected", 0.7F, p -> p.getOwnedMegas().size()),
    ZCRYSTALS_OWNED("Z-Crystals Collected", 1.0F, p -> p.getInventory().getZCrystals().size()),
    DUELS_WON("Duels Won", 0.2F, p -> p.getStatistics().get(StatisticType.PVP_DUELS_WON) + p.getStatistics().get(StatisticType.TRAINER_DUELS_WON) + p.getStatistics().get(StatisticType.ELITE_DUELS_WON) * 2 + p.getStatistics().get(StatisticType.RAIDS_WON) * 2 + p.getStatistics().get(StatisticType.WILD_DUELS_WON) / 2)

    ;

    private final String componentName;
    private final float weight;
    private final Function<PlayerDataQuery, Integer> calculator;

    LeaderboardScoreComponent(String name, float weight, Function<PlayerDataQuery, Integer> calculator)
    {
        this.componentName = name;
        this.weight = weight;
        this.calculator = calculator;
    }

    public String getName()
    {
        return this.componentName;
    }

    public float getWeight()
    {
        return this.weight;
    }

    public float compute(PlayerDataQuery player)
    {
        return this.weight * this.calculator.apply(player);
    }
}
