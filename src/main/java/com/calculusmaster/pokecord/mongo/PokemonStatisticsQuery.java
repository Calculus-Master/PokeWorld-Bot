package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.enums.PokemonStatistic;
import com.mongodb.client.model.Updates;
import org.bson.Document;

public class PokemonStatisticsQuery extends MongoQuery
{
    public PokemonStatisticsQuery(String UUID)
    {
        super("UUID", UUID, Mongo.PokemonStatisticsData);
    }

    public static void register(String UUID)
    {
        Document statsData = new Document("UUID", UUID);

        for(PokemonStatistic s : PokemonStatistic.values()) statsData.append(s.key, 0);

        Mongo.PokemonStatisticsData.insertOne(statsData);
    }

    private void update()
    {
        this.document = Mongo.PokemonStatisticsData.find(this.query).first();
    }

    public int get(PokemonStatistic stat)
    {
        return this.json().getInt(stat.key);
    }

    public void incr(PokemonStatistic stat, int amount)
    {
        Mongo.PokemonStatisticsData.updateOne(this.query, Updates.inc(stat.key, amount));

        this.update();
    }

    public void incr(PokemonStatistic stat)
    {
        this.incr(stat, 1);
    }

}
