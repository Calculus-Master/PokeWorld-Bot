package com.calculusmaster.pokecord.game.world;

import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PokeWorldMarket
{
    private static final ExecutorService UPDATER = Executors.newFixedThreadPool(2);

    private static final Map<String, MarketEntry> ENTRIES = new HashMap<>();
    private static final Map<String, MarketEntry> POKEMON_ENTRIES = new HashMap<>();
    private static final Map<String, List<MarketEntry>> LISTINGS = new HashMap<>();

    public static void init()
    {
        try(ExecutorService pool = Executors.newFixedThreadPool(5))
        {
            Mongo.MarketData.find().forEach(d -> pool.submit(() -> PokeWorldMarket.addMarketEntry(d)));
        }
    }

    //Market Entry Modifiers
    private static void addMarketEntry(Document data)
    {
        PokeWorldMarket.addMarketEntry(new MarketEntry(data));
    }

    public static void insertMarketEntry(MarketEntry entry)
    {
        PokeWorldMarket.addMarketEntry(entry);

        UPDATER.submit(() -> Mongo.MarketData.insertOne(entry.serialize()));
    }

    public static void addMarketEntry(MarketEntry entry)
    {
        ENTRIES.put(entry.getMarketID(), entry);
        POKEMON_ENTRIES.put(entry.getPokemonID(), entry);

        if(!LISTINGS.containsKey(entry.getSellerID())) LISTINGS.put(entry.getSellerID(), new ArrayList<>());
        LISTINGS.get(entry.getSellerID()).add(entry);
    }

    public static void deleteMarketEntry(String marketID)
    {
        MarketEntry entry = ENTRIES.remove(marketID);

        POKEMON_ENTRIES.remove(entry.getPokemonID());
        LISTINGS.get(entry.getSellerID()).remove(entry);

        UPDATER.submit(() -> Mongo.MarketData.deleteOne(Filters.eq("marketID", marketID)));
    }

    //Accessors
    public static MarketEntry getMarketEntry(String marketID)
    {
        return ENTRIES.get(marketID);
    }

    public static MarketEntry getMarketEntry(Pokemon p)
    {
        return POKEMON_ENTRIES.get(p.getUUID());
    }

    public static List<Pokemon> getPokemon()
    {
        return new ArrayList<>(ENTRIES.values().stream().map(MarketEntry::getPokemon).toList());
    }

    public static List<MarketEntry> getListings(String playerID)
    {
        return LISTINGS.getOrDefault(playerID, new ArrayList<>());
    }
}
