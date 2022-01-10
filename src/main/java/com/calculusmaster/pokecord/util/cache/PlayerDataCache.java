package com.calculusmaster.pokecord.util.cache;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.helpers.CacheHelper;

public class PlayerDataCache
{
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

        CacheHelper.PLAYER_DATA.put(ID, this);
    }

    public PlayerDataQuery data()
    {
        return this.data;
    }
}
