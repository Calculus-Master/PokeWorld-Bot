package com.calculusmaster.pokecord.game.player.leaderboard;

import com.calculusmaster.pokecord.mongo.PlayerData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PlayerScoreData
{
    private final PlayerData playerData;
    private final Map<LeaderboardScoreComponent, Float> scores;
    private float totalScore;

    public PlayerScoreData(String playerID)
    {
        this.playerData = PlayerData.build(playerID);
        this.scores = new HashMap<>(); Arrays.stream(LeaderboardScoreComponent.values()).forEach(c -> this.scores.put(c, 0F));
        this.totalScore = 0F;
    }

    public void calculate()
    {
        Arrays.stream(LeaderboardScoreComponent.values()).forEach(component -> this.scores.put(component, component.compute(this.playerData)));

        this.totalScore = (float)this.scores.values().stream().mapToDouble(f -> f).sum();
    }

    public PlayerData getPlayerData()
    {
        return this.playerData;
    }

    public float getScore()
    {
        return this.totalScore;
    }

    public float getScore(LeaderboardScoreComponent component)
    {
        return this.scores.get(component);
    }
}
