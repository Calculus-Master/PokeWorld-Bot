package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.game.enums.functional.Achievement;
import com.calculusmaster.pokecord.game.trade.elements.MarketEntry;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.mongodb.client.model.Filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CacheHelper
{
    //If true, pokemon list caching will be done the first time the player uses p!p, rather than all at the bot init (to lower load times)
    public static boolean DYNAMIC_CACHING_ACTIVE;

    //Stored Data Type: MarketEntry
    public static final List<MarketEntry> MARKET_ENTRIES = new ArrayList<>();

    //Stored Data Type: Player IDs
    public static final Map<Achievement, List<String>> ACHIEVEMENT_CACHE = new HashMap<>();

    public static void initMarketEntries()
    {
        List<String> IDs = new ArrayList<>();
        Mongo.MarketData.find(Filters.exists("marketID")).forEach(d -> IDs.add(d.getString("marketID")));

        if(IDs.size() == 0) return;

        int split = 20;
        List<List<String>> totalList = new ArrayList<>();

        for (int j = 0; j < IDs.size(); j += split)
        {
            totalList.add(IDs.subList(j, Math.min(j + split, IDs.size())));
        }

        int threads = IDs.size() < split ? 1 : IDs.size() / split;

        ExecutorService pool = Executors.newFixedThreadPool(threads);

        for(List<String> l : totalList)
        {
            try {Thread.sleep(100);}
            catch (Exception e) { LoggerHelper.reportError(CacheHelper.class, "Market Entry Init Error! Thread cannot be slept!", e); }

            pool.execute(() -> { for(String s : l) MARKET_ENTRIES.add(MarketEntry.build(s)); });
        }

        pool.shutdown();

        try { pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); }
        catch (Exception e) { LoggerHelper.reportError(CacheHelper.class, "Market Entry List init failed!", e); }
    }

    public static void initAchievementCache()
    {
        for(Achievement a : Achievement.values()) ACHIEVEMENT_CACHE.put(a, new ArrayList<>());
    }
}
