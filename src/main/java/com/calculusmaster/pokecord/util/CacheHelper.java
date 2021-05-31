package com.calculusmaster.pokecord.util;

import com.calculusmaster.pokecord.commands.pokemon.CommandPokemon;
import com.calculusmaster.pokecord.game.Pokemon;
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

    public static void main(String[] args)
    {
        long i = System.currentTimeMillis();
        initPokemonLists();
        System.out.println("New Init: " + (System.currentTimeMillis() - i) + "ms");
    }

    //Pokemon List Updates
    public static void addPokemon(String player, String UUID)
    {
        List<Pokemon> pokemon = POKEMON_LISTS.get(player);
        List<String> uuids = UUID_LISTS.get(player);

        pokemon.add(Pokemon.buildCore(UUID, pokemon.size()));
        uuids.add(UUID);

        POKEMON_LISTS.put(player, pokemon);
        UUID_LISTS.put(player, uuids);
    }

    public static void removePokemon(String player, String UUID)
    {
        List<Pokemon> pokemon = POKEMON_LISTS.get(player);
        List<String> uuids = UUID_LISTS.get(player);

        int index = uuids.indexOf(UUID);
        pokemon.remove(index);
        uuids.remove(index);

        for(Pokemon p : pokemon) p.setNumber(uuids.indexOf(p.getUUID()) + 1);

        POKEMON_LISTS.put(player, pokemon);
        UUID_LISTS.put(player, uuids);
    }

    public static void updatePokemon(String UUID)
    {
        String player = "";
        for(String p : UUID_LISTS.keySet()) if(UUID_LISTS.get(p).contains(UUID)) player = p;

        List<Pokemon> pokemon = POKEMON_LISTS.get(player);
        int index = UUID_LISTS.get(player).indexOf(UUID);
        pokemon.set(index, Pokemon.buildCore(UUID, index + 1));

        POKEMON_LISTS.put(player, pokemon);
    }

    public static void createPokemonList(String player)
    {
        List<Pokemon> list = new ArrayList<>();
        for(int i = 0; i < UUID_LISTS.get(player).size(); i++) list.add(Pokemon.buildCore(UUID_LISTS.get(player).get(i), i + 1));
        POKEMON_LISTS.put(player, list);
    }

    public static void setUUIDLists()
    {
        long i = System.currentTimeMillis();
        Mongo.PlayerData.find(Filters.exists("playerID")).forEach(d -> UUID_LISTS.put(d.getString("playerID"), d.getList("pokemon", String.class)));
        System.out.println("UUID List Init: " + (System.currentTimeMillis() - i) + "ms!");
    }

    public static void initPokemonLists()
    {
        long initialTime = System.currentTimeMillis();

        List<String> players = new ArrayList<>();
        Mongo.PlayerData.find(Filters.exists("username")).forEach(d -> players.add(d.getString("playerID")));

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

        System.out.println("Pokemon List Init: " + (initialTime - finalTime) + "ms!");
    }
}
