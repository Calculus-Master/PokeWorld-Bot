package com.calculusmaster.pokecord.util.cache;

import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PokemonDataCache
{
    public static final Map<String, Document> CACHE = new HashMap<>();

    public static void init()
    {
        ExecutorService pool = Executors.newCachedThreadPool();

        Mongo.PokemonData.find().forEach(d -> pool.execute(() -> PokemonDataCache.addCacheData(d.getString("UUID"), d)));

        pool.shutdown();

        try { pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS); }
        catch (Exception e) { LoggerHelper.reportError(PokemonDataCache.class, "New Cacher Init Failed!", e); }
    }

    public static void addCacheData(String UUID, Document cache)
    {
        Collections.synchronizedMap(CACHE).put(UUID, cache);
    }

    public synchronized static Document getCache(String UUID)
    {
        return Collections.synchronizedMap(CACHE).get(UUID);
    }

    public synchronized static void updateCache(String UUID)
    {
        Collections.synchronizedMap(CACHE).put(UUID, Mongo.PokemonData.find(Filters.eq("UUID", UUID)).first());
    }

    public synchronized static void removeCache(String UUID)
    {
        Collections.synchronizedMap(CACHE).remove(UUID);
    }
}
