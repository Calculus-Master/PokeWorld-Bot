package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

public class PlayerStatisticsQuery extends MongoQuery
{
    public PlayerStatisticsQuery(String playerID)
    {
        super("playerID", playerID, Mongo.StatisticsData);
    }

    public static void register(String playerID)
    {
        Document statsData = new Document("playerID", playerID);

        for(PlayerStatistic s : PlayerStatistic.values()) statsData.append(s.key, 0);

        LoggerHelper.logDatabaseInsert(PlayerStatisticsQuery.class, statsData);

        Mongo.StatisticsData.insertOne(statsData);
    }

    @Override
    protected void update(Bson updates)
    {
        this.document = Mongo.StatisticsData.find(this.query).first();
    }

    public int get(PlayerStatistic stat)
    {
        return this.document.getInteger(stat.key);
    }

    public void incr(PlayerStatistic stat, int amount)
    {
        Mongo.StatisticsData.updateOne(this.query, Updates.inc(stat.key, amount));

        this.update(null);
    }

    public void incr(PlayerStatistic stat)
    {
        this.incr(stat, 1);
    }
}
