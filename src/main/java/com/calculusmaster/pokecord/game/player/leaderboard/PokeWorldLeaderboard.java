package com.calculusmaster.pokecord.game.player.leaderboard;

import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.mongodb.client.model.Projections;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PokeWorldLeaderboard
{
    private static final ScheduledExecutorService UPDATER = Executors.newSingleThreadScheduledExecutor();
    private static final ExecutorService MANAGER = Executors.newFixedThreadPool(4);

    private static PokeWorldLeaderboard CURRENT;
    private static List<String> PLAYERS_UPDATED = new ArrayList<>();
    private static final int TOP_PLAYERS = 10;

    public static void init()
    {
        PokeWorldLeaderboard.updateLeaderboard();

        UPDATER.scheduleAtFixedRate(PokeWorldLeaderboard::updateLeaderboard, 4, 4, TimeUnit.HOURS);
    }

    public static void updateLeaderboard()
    {
        long timeI = System.currentTimeMillis();

        if(CURRENT == null) CURRENT = new PokeWorldLeaderboard();

        CURRENT.recalculate();

        LoggerHelper.time(PokeWorldLeaderboard.class, "Updating PokeWorld Leaderboard", timeI, System.currentTimeMillis());
    }

    public static PokeWorldLeaderboard getCurrent()
    {
        return CURRENT;
    }

    public static void addUpdatedPlayer(String playerID)
    {
        PLAYERS_UPDATED.add(playerID);
    }

    //Class
    private final Map<String, PlayerScoreData> scores;
    private final List<String> rankings;
    private long timestamp;

    public PokeWorldLeaderboard()
    {
        this.scores = new HashMap<>();
        this.rankings = new ArrayList<>();
        this.timestamp = Global.timeNowEpoch();
    }

    public void recalculate()
    {
        Mongo.PlayerData.find().projection(Projections.include("playerID")).forEach(d -> {
            String playerID = d.getString("playerID");
            if(!this.scores.containsKey(playerID)) this.scores.put(playerID, new PlayerScoreData(playerID));
        });

        this.scores
                .entrySet()
                .stream()
                .filter(e -> !PLAYERS_UPDATED.contains(e.getKey())) //Skip players that don't need an update
                .map(Map.Entry::getValue)//Get PlayerScoreData
                .forEach(data -> MANAGER.submit(data::calculate)); //Calculate

        this.determineTopPlayers();

        this.timestamp = Global.timeNowEpoch();

        PLAYERS_UPDATED.clear();
    }

    private void determineTopPlayers()
    {
        this.rankings.clear();

        this.scores.entrySet()
                .stream()
                .sorted((e1, e2) -> (int)(e2.getValue().getScore() - e1.getValue().getScore()))
                .forEach(e -> this.rankings.add(e.getKey()));
    }

    //Returns Pairs where left is Rank and Username, right is Score
    public Pair<String, String> getTop()
    {
        String left = IntStream
                .range(0, Math.min(TOP_PLAYERS, this.rankings.size()))
                .mapToObj(i -> "%s%s: **%s**%s".formatted(
                        i == 0 ? "__" : "",
                        i + 1,
                        this.scores.get(this.rankings.get(i)).getPlayerData().getUsername(),
                        i == 0 ? "__" : "")
                )
                .collect(Collectors.joining("\n"));

        String right = this.rankings
                .stream()
                .limit(TOP_PLAYERS)
                .map(i -> String.format("%.2f", this.scores.get(i).getScore()))
                .collect(Collectors.joining("\n"));

        return new Pair<>(left, right);
    }

    public PlayerScoreData getScoreData(String playerID)
    {
        return this.scores.get(playerID);
    }

    public String getTimestamp()
    {
        return "<t:%s:f> (<t:%s:R>)".formatted(this.timestamp, this.timestamp);
    }

    public int getRanking(String playerID)
    {
        return this.rankings.indexOf(playerID) + 1;
    }

    public int size()
    {
        return this.rankings.size();
    }
}
