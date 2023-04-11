package com.calculusmaster.pokecord.game.player.leaderboard;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

import java.util.function.Function;

public enum LeaderboardScoreComponent
{
    POKEDEX_REGISTERED("Pokemon Registered", 0.6F, p -> p.getPokedex().getSize()),
    MASTERY_LEVEL("Pokemon Mastery Level", 0.4F, PlayerDataQuery::getLevel),
    ACHIEVEMENTS("Achievements", 0.8F, p -> p.getAchievements().size()),

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
