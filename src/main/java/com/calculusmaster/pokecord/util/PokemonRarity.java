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

        shuffleSpawns();
    }

    public static String getSpawn()
    {
        return SPAWNS.get(new Random().nextInt(SPAWNS.size()));
    }

    public static void add(String name, Rarity r)
    {
        for(int i = 0; i < r.num; i++) SPAWNS.add(name);
    }

    private static void shuffleSpawns()
    {
        List<String> spawnsOld = new ArrayList<>(List.copyOf(SPAWNS));
        SPAWNS.clear();
        Random r = new Random();

        while(spawnsOld.size() > 0)
        {
            String s = spawnsOld.get(r.nextInt(spawnsOld.size()));
            SPAWNS.add(s);
            spawnsOld.remove(s);
        }
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
