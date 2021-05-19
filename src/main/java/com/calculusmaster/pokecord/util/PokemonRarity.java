package com.calculusmaster.pokecord.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PokemonRarity
{
    public static final List<String> SPAWNS = new ArrayList<>();

    public static void init()
    {
        PokemonRarity.add("Bulbasaur", Rarity.COPPER);
        PokemonRarity.add("Ivysaur", Rarity.SILVER);
        PokemonRarity.add("Venusaur", Rarity.GOLD);
        PokemonRarity.add("Charmander", Rarity.COPPER);
        PokemonRarity.add("Charmeleon", Rarity.SILVER);
        PokemonRarity.add("Charizard", Rarity.GOLD);
        PokemonRarity.add("Squirtle", Rarity.COPPER);
        PokemonRarity.add("Wartortle", Rarity.SILVER);
        PokemonRarity.add("Blastoise", Rarity.GOLD);
        PokemonRarity.add("Caterpie", Rarity.COPPER);
        PokemonRarity.add("Metapod", Rarity.COPPER);
        PokemonRarity.add("Butterfree", Rarity.SILVER);
        PokemonRarity.add("Weedle", Rarity.COPPER);
        PokemonRarity.add("Kakuna", Rarity.COPPER);
        PokemonRarity.add("Beedrill", Rarity.GOLD);
        PokemonRarity.add("Pidgey", Rarity.COPPER);
        PokemonRarity.add("Pidgeotto", Rarity.SILVER);
        PokemonRarity.add("Pidgeot", Rarity.SILVER);
        PokemonRarity.add("Rattata", Rarity.COPPER);
        PokemonRarity.add("Alolan Rattata", Rarity.SILVER);
        PokemonRarity.add("Raticate", Rarity.SILVER);
        PokemonRarity.add("Alolan Raticate", Rarity.GOLD);
        PokemonRarity.add("Spearow", Rarity.COPPER);
        PokemonRarity.add("Fearow", Rarity.SILVER);
        PokemonRarity.add("Ekans", Rarity.COPPER);
        PokemonRarity.add("Arbok", Rarity.SILVER);

        Collections.shuffle(SPAWNS);
        //System.out.println(SPAWNS.stream().map(n -> n.substring(0, 2)).toString());
    }

    public static String getSpawn()
    {
        return SPAWNS.get(new Random().nextInt(SPAWNS.size()));
    }

    public static void add(String name, Rarity r)
    {
        for(int i = 0; i < r.num; i++) SPAWNS.add(name);
    }

    public enum Rarity
    {
        COPPER(100),
        SILVER(75),
        GOLD(50),
        DIAMOND(25),
        PLATINUM(15),
        MYTHICAL(10),
        LEGENDARY(5);

        public int num;
        Rarity(int num)
        {
            this.num = num;
        }
    }
}
