package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.util.Mongo;
import org.bson.Document;

public class PlayerStatisticsQuery extends MongoQuery
{
    public PlayerStatisticsQuery(String playerID)
    {
        super("playerID", playerID, Mongo.PlayerStatisticsData);
    }

    /* Planning
        - pokemon caught
        - pvp duels won
        - wild duels won
        - trainer duels won
        - elite trainer duels won
        - total credits earned
        - total credits spent
        - total redeems earned
        - total redeems spent
        - pokemon sold on market
        - pokemon bought from market
        - trades done
        - number of items bought from shop
     */

    public static void register(String playerID)
    {
        Document statsData = new Document()
                .append("playerID", playerID);

        Mongo.SettingsData.insertOne(statsData);
    }
}
