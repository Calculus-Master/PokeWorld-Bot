package com.calculusmaster.pokecord.mongo.cache;

import com.calculusmaster.pokecord.mongo.PlayerData;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public class CacheHandler
{
    public static final int MAX_CACHE_SIZE_PLAYER_DATA = 5000;

    public static final Cache<String, PlayerData> PLAYER_DATA = Caffeine.newBuilder()
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .maximumSize(MAX_CACHE_SIZE_PLAYER_DATA)
            .recordStats()
            .evictionListener((key, value, cause) -> LoggerHelper.warn(CacheHandler.class, "Cache Removal: Player Data | ID: " + key + " | Cause: " + cause))
            .removalListener((key, value, cause) -> LoggerHelper.warn(CacheHandler.class, "Cache Removal: Player Data | ID: " + key + " | Cause: " + cause))
            .build();
}
