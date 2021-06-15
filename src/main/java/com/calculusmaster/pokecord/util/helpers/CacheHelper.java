package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.commands.pokemon.CommandPokemon;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CacheHelper
{
    //Key: playerID     Value: Pokemon List
    public static final Map<String, List<String>> UUID_LISTS = new HashMap<>();
    public static final Map<String, List<Pokemon>> POKEMON_LISTS = new HashMap<>();
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

        pokemon.add(Pokemon.buildCore(UUID, pokemon.size()));
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
        pokemon.set(index, Pokemon.buildCore(UUID, index + 1));

        updateNumbers(player);

        POKEMON_LISTS.put(player, pokemon);
    }

    public static void updateNumbers(String player)
    {
        for(Pokemon p : POKEMON_LISTS.get(player)) p.setNumber(UUID_LISTS.get(player).indexOf(p.getUUID()) + 1);
    }

    private static void initialList(String player)
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

        System.out.println("Initialization for Player: " + player + " took " + (System.currentTimeMillis() - init));
    }

    public static void setUUIDLists()
    {
        Mongo.PlayerData.find(Filters.exists("playerID")).forEach(d -> UUID_LISTS.put(d.getString("playerID"), d.getList("pokemon", String.class)));
    }

    public static void initPokemonLists()
    {
        long initialTime = System.currentTimeMillis();

        List<String> players = new ArrayList<>();
        Mongo.PlayerData.find(Filters.exists("username")).forEach(d -> players.add(d.getString("playerID")));

        if(players.size() == 0) return;

        setUUIDLists();

        ExecutorService pool = Executors.newFixedThreadPool(players.size() / 2);

        for(String p : players)
        {
            pool.execute(() -> createPokemonList(p));
        }

        pool.shutdown();

        try { pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); }
        catch (Exception e) { System.out.println("Pokemon List Init failed!"); }

        long finalTime = System.currentTimeMillis();

        System.out.println("Pokemon List Init: " + (finalTime - initialTime) + "ms!");
    }
}
