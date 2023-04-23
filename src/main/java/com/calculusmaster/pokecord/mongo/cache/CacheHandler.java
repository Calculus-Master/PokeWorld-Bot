package com.calculusmaster.pokecord.mongo.cache;

import com.calculusmaster.pokecord.game.pokemon.evolution.PokemonEgg;
import com.calculusmaster.pokecord.mongo.PlayerData;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.bson.Document;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class CacheHandler
{
    public static final int MAX_CACHE_SIZE_PLAYER_DATA = 5000;
    public static final int MAX_CACHE_SIZE_POKEMON_DATA = 20_000;
    public static final int MAX_CACHE_SIZE_EGGS = MAX_CACHE_SIZE_PLAYER_DATA * PokemonEgg.MAX_EGGS;

    public static final Cache<String, PlayerData> PLAYER_DATA = Caffeine.newBuilder()
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .maximumSize(MAX_CACHE_SIZE_PLAYER_DATA)
            .initialCapacity(25)
            .recordStats()
            .evictionListener((key, value, cause) -> LoggerHelper.warn(CacheHandler.class, "Cache Removal: Player Data | ID: " + key + " | Cause: " + cause))
            .removalListener((key, value, cause) -> LoggerHelper.warn(CacheHandler.class, "Cache Removal: Player Data | ID: " + key + " | Cause: " + cause))
            .build();

    public static final Cache<String, Document> POKEMON_DATA = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofDays(1))
            .maximumSize(MAX_CACHE_SIZE_POKEMON_DATA)
            .initialCapacity(200)
            .recordStats()
            .evictionListener((key, value, cause) -> LoggerHelper.warn(CacheHandler.class, "Cache Removal: Pokemon Data | UUID: " + key + " | Cause: " + cause))
            .removalListener((key, value, cause) -> LoggerHelper.warn(CacheHandler.class, "Cache Removal: Pokemon Data | UUID: " + key + " | Cause: " + cause))
            .build();

    public static final Cache<String, Document> EGG_DATA = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofDays(1))
            .maximumSize(MAX_CACHE_SIZE_EGGS)
            .initialCapacity(200)
            .evictionListener((key, value, cause) -> LoggerHelper.warn(CacheHandler.class, "Cache Removal: Egg Data | UUID: " + key + " | Cause: " + cause))
            .removalListener((key, value, cause) -> LoggerHelper.warn(CacheHandler.class, "Cache Removal: Egg Data | UUID: " + key + " | Cause: " + cause))
            .build();

}
