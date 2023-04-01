package com.calculusmaster.pokecord.util.cacheold;

import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

import java.util.HashMap;
import java.util.Map;

public class PlayerDataCache
{
    public static final Map<String, PlayerDataCache> CACHE = new HashMap<>();

    public static void init()
    {
        Mongo.PlayerData.find().forEach(d -> new PlayerDataCache(d.getString("playerID")));
    }

    public static void addCache(String ID)
    {
        new PlayerDataCache(ID);
    }

    private final PlayerDataQuery data;

    public PlayerDataCache(String ID)
    {
        this.data = new PlayerDataQuery(ID);

        PlayerDataCache.CACHE.put(ID, this);
    }

    public PlayerDataQuery data()
    {
        return this.data;
    }
}
