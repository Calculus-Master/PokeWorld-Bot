package com.calculusmaster.pokecord.game.world;

import com.calculusmaster.pokecord.game.enums.elements.Region;
import com.calculusmaster.pokecord.game.enums.elements.Time;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

public class RegionManager
{
    private static Region REGION;
    private static final Bson REGION_DATA_QUERY = Filters.eq("type", "region");

    public static void init()
    {
        Document data = Mongo.MiscData.find(REGION_DATA_QUERY).first();

        if(data == null)
        {
            LoggerHelper.warn(RegionManager.class, "No Region Data found in Database. Creating new Document.");

            data = new Document("type", "region").append("region", Region.KANTO.toString());
            Mongo.MiscData.insertOne(data);
        }

        REGION = Region.valueOf(data.getString("region"));
        PokemonRarity.updateSpawnWeights(REGION);
    }

    public static void updateRegion()
    {
        int nextIndex = REGION.ordinal() + 1;
        if(nextIndex >= Region.values().length) nextIndex = 0;

        REGION = Region.values()[nextIndex];
        Mongo.MiscData.updateOne(REGION_DATA_QUERY, Updates.set("region", REGION.toString()));

        PokemonRarity.updateSpawnWeights(REGION);

        LoggerHelper.info(RegionManager.class, "Global Region is now " + REGION.toString() + "! Spawn Weights have been updated.");
    }

    public static Region getCurrentRegion()
    {
        return REGION;
    }

    public static Time getCurrentTime()
    {
        int hour = Global.timeNow().getHour();

        if(true) return Time.NIGHT;

        if(hour == 17 || hour == 18) return Time.DUSK;
        else if(hour <= 5 || hour >= 19) return Time.NIGHT;
        else return Time.DAY;
    }
}
