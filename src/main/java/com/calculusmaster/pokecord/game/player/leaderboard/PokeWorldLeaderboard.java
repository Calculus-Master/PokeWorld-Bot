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
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PokeWorldLeaderboard
{
    private static final ScheduledExecutorService UPDATER = Executors.newSingleThreadScheduledExecutor();
    private static final ExecutorService MANAGER = Executors.newFixedThreadPool(4);

    private static PokeWorldLeaderboard CURRENT;
    private static final List<String> PLAYERS_UPDATED = new ArrayList<>();
    private static final int TOP_PLAYERS = 10;

    public static void init()
    {
        PokeWorldLeaderboard.updateLeaderboard();

        UPDATER.scheduleAtFixedRate(PokeWorldLeaderboard::updateLeaderboard, 4, 4, TimeUnit.HOURS);
    }

    public static void updateLeaderboard()
    {
        long timeI = System.currentTimeMillis();

        boolean initial = CURRENT == null;
        if(initial) CURRENT = new PokeWorldLeaderboard();

        CURRENT.recalculate(initial);

        LoggerHelper.time(PokeWorldLeaderboard.class, "Updating PokeWorld Leaderboard", timeI, System.currentTimeMillis());
    }

    public static PokeWorldLeaderboard getCurrent()
    {
        return CURRENT;
    }

    public static synchronized void addUpdatedPlayer(String playerID)
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

    public void recalculate(boolean initial)
    {
        Mongo.PlayerData.find().projection(Projections.include("playerID")).forEach(d -> {
            String playerID = d.getString("playerID");
            if(!this.scores.containsKey(playerID)) this.scores.put(playerID, new PlayerScoreData(playerID));
        });

        List<Callable<Object>> calculationTasks = new ArrayList<>();

        this.scores
                .entrySet()
                .stream()
                .filter(e -> initial || PLAYERS_UPDATED.contains(e.getKey())) //Skip players that don't need an update
                .map(Map.Entry::getValue) //Get PlayerScoreData
                .forEach(data -> calculationTasks.add(Executors.callable(data::calculate))); //Calculate

        PLAYERS_UPDATED.clear();

        try { MANAGER.invokeAll(calculationTasks); }
        catch (InterruptedException e) { e.printStackTrace(); LoggerHelper.error(PokeWorldLeaderboard.class, "Thread Pool interrupted trying to calculate leaderboard scores."); }

        this.determineTopPlayers();
        this.timestamp = Global.timeNowEpoch();
    }

    private void determineTopPlayers()
    {
        this.rankings.clear();

        this.rankings.addAll(this.scores.keySet());

        this.rankings.sort((p1, p2) -> (int)(this.scores.get(p2).getScore() - this.scores.get(p1).getScore()));
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
