package com.calculusmaster.pokecord.util;

import com.calculusmaster.pokecord.game.Pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PokemonRarity
{
    public static final List<PokemonRarity> RARITIES = new ArrayList<>();
    public static final List<String> SPAWNS = new ArrayList<>();

    public static void init()
    {
        PokemonRarity Bulbasaur = new PokemonRarity("Bulbasaur", Rarity.COPPER);
        PokemonRarity Ivysaur = new PokemonRarity("Ivysaur", Rarity.SILVER);
        PokemonRarity Venusaur = new PokemonRarity("Venusaur", Rarity.GOLD);
    }

    public static String getSpawn()
    {
        return SPAWNS.get(new Random().nextInt(SPAWNS.size()));
    }

    public PokemonRarity(String name, Rarity rarity)
    {
        RARITIES.add(this);
        for(int i = 0; i < rarity.num; i++) SPAWNS.add(name);
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
