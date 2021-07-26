package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.trade.elements.MarketEntry;
import com.calculusmaster.pokecord.util.Mongo;
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

    //Key: playerID     Value: Pokemon List
    public static final Map<String, List<String>> UUID_LISTS = new HashMap<>();
    public static final Map<String, List<Pokemon>> POKEMON_LISTS = new HashMap<>();

    //Stored Data Type: MarketEntryp
    public static final List<MarketEntry> MARKET_ENTRIES = new ArrayList<>();

    //Stored Data Type: Player IDs
    public static final Map<Achievements, List<String>> ACHIEVEMENT_CACHE = new HashMap<>();

    //Key: playerID     Value: Team
    public static final Map<String, List<Pokemon>> TEAM_LISTS = new HashMap<>();

    //Pokemon List Updates
    public static void addPokemon(String player, String UUID)
    {
        List<Pokemon> pokemon = POKEMON_LISTS.get(player);
        List<String> uuids = UUID_LISTS.get(player);

        if(pokemon == null)
        {
            initialList(player);
            pokemon = POKEMON_LISTS.get(player);
            uuids = UUID_LISTS.get(player);
        }

        pokemon.add(Pokemon.build(UUID));
        uuids.add(UUID);

        updateNumbers(player);

        POKEMON_LISTS.put(player, pokemon);
        UUID_LISTS.put(player, uuids);
    }

    public static void removePokemon(String player, String UUID)
    {
        List<Pokemon> pokemon = POKEMON_LISTS.get(player);
        List<String> uuids = UUID_LISTS.get(player);

        if(pokemon == null)
        {
            initialList(player);
            pokemon = POKEMON_LISTS.get(player);
            uuids = UUID_LISTS.get(player);
        }

        int index = uuids.indexOf(UUID);
        pokemon.remove(index);
        uuids.remove(index);

        updateNumbers(player);

        POKEMON_LISTS.put(player, pokemon);
        UUID_LISTS.put(player, uuids);
    }

    public static void updatePokemon(String UUID)
    {
        String player = "";
        for(String p : UUID_LISTS.keySet()) if(UUID_LISTS.get(p).contains(UUID)) player = p;

        if(player.equals(""))
        {
            System.out.println(UUID);
            return;
        }

        List<Pokemon> pokemon = POKEMON_LISTS.get(player);
        int index = UUID_LISTS.get(player).indexOf(UUID);

        try { pokemon.set(index, Pokemon.buildCore(UUID, index + 1)); }
        catch (NullPointerException e)
        {
            if(DYNAMIC_CACHING_ACTIVE) System.out.println("Update Pokemon failed (Dynamic Caching is Active!");
            else
            {
                System.out.println("DYNAMIC CACHING IS NOT ACTIVE!");
                e.printStackTrace();
            }
        }

        updateNumbers(player);

        POKEMON_LISTS.put(player, pokemon);
    }

    public static void updateNumbers(String player)
    {
        for(Pokemon p : POKEMON_LISTS.get(player)) p.setNumber(UUID_LISTS.get(player).indexOf(p.getUUID()) + 1);
    }

    public static void initialList(String player)
    {
        POKEMON_LISTS.put(player, new ArrayList<>());
        UUID_LISTS.put(player, new ArrayList<>());
    }

    public static void createPokemonList(String player)
    {
        long init = System.currentTimeMillis();

        List<Pokemon> list = new ArrayList<>();
        for(int i = 0; i < UUID_LISTS.get(player).size(); i++) list.add(Pokemon.buildCore(UUID_LISTS.get(player).get(i), i + 1));
        POKEMON_LISTS.put(player, list);

        LoggerHelper.info(CacheHelper.class, "Pokemon List Init Complete - " + player + " (" + (System.currentTimeMillis() - init) + "ms)!");
    }

    public static void setUUIDLists()
    {
        Mongo.PlayerData.find(Filters.exists("playerID")).forEach(d -> UUID_LISTS.put(d.getString("playerID"), d.getList("pokemon", String.class)));
    }

    public static void initPokemonLists()
    {
        List<String> players = new ArrayList<>();
        Mongo.PlayerData.find(Filters.exists("username")).forEach(d -> players.add(d.getString("playerID")));

        if(players.size() == 0) return;

        setUUIDLists();

        ExecutorService pool = Executors.newFixedThreadPool(players.size() / 2);

        for(String p : players)
        {
            if(!DYNAMIC_CACHING_ACTIVE) pool.execute(() -> createPokemonList(p));
        }

        pool.shutdown();

        try { pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); }
        catch (Exception e) { System.out.println("Pokemon List Init failed!"); }
    }

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
            catch (Exception e) {System.out.println("Can't Sleep Thread!");}

            pool.execute(() -> { for(String s : l) MARKET_ENTRIES.add(MarketEntry.build(s)); });
        }

        pool.shutdown();

        try { pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); }
        catch (Exception e) { System.out.println("CommandMarket Init failed!"); }
    }

    public static void initAchievementCache()
    {
        for(Achievements a : Achievements.values()) ACHIEVEMENT_CACHE.put(a, new ArrayList<>());
    }
}
