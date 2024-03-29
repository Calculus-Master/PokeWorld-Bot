package com.calculusmaster.pokecord.game.player.components;

import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.mongo.PlayerData;
import com.calculusmaster.pokecord.util.enums.StatisticType;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerStatisticsRecord
{
    private static final ExecutorService UPDATER = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("PlayerData-StatisticsUpdates-", 0).factory());

    private final PlayerData playerData;
    private final Map<StatisticType, Integer> statistics;

    public PlayerStatisticsRecord(PlayerData playerData)
    {
        this.playerData = playerData;
        this.statistics = new HashMap<>();
    }

    public PlayerStatisticsRecord(PlayerData playerData, Document data)
    {
        this(playerData);

        data.forEach((s, o) -> this.statistics.put(StatisticType.valueOf(s.toUpperCase()), (Integer)o));
    }

    public Document serialize()
    {
        Document data = new Document();
        this.statistics.forEach((s, i) -> data.append(s.toString(), i));
        return data;
    }

    //Accessors and Updaters
    public int get(StatisticType type)
    {
        return this.statistics.getOrDefault(type, 0);
    }

    public void increase(StatisticType type, int amount)
    {
        this.statistics.put(type, this.get(type) + amount);

        UPDATER.submit(() -> Mongo.PlayerData.updateOne(Filters.eq("playerID", this.playerData.getID()), Updates.inc("statistics." + type.toString(), amount)));
    }

    public void increase(StatisticType type)
    {
        this.increase(type, 1);
    }
}
