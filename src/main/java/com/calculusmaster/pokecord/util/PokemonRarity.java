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
        PokemonRarity.add("Bulbasaur", Rarity.SILVER);
        PokemonRarity.add("Ivysaur", Rarity.GOLD);
        PokemonRarity.add("Venusaur", Rarity.DIAMOND);
        PokemonRarity.add("Charmander", Rarity.SILVER);
        PokemonRarity.add("Charmeleon", Rarity.GOLD);
        PokemonRarity.add("Charizard", Rarity.DIAMOND);
        PokemonRarity.add("Squirtle", Rarity.SILVER);
        PokemonRarity.add("Wartortle", Rarity.GOLD);
        PokemonRarity.add("Blastoise", Rarity.DIAMOND);
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
        PokemonRarity.add("Pikachu", Rarity.SILVER);
        PokemonRarity.add("Raichu", Rarity.GOLD);
        PokemonRarity.add("Alolan Raichu", Rarity.DIAMOND);
        PokemonRarity.add("Sandshrew", Rarity.COPPER);
        PokemonRarity.add("Alolan Sandshrew", Rarity.SILVER);
        PokemonRarity.add("Sandslash", Rarity.SILVER);
        PokemonRarity.add("Alolan Sandslash", Rarity.GOLD);
        PokemonRarity.add("NidoranF", Rarity.COPPER);
        PokemonRarity.add("Nidorina", Rarity.SILVER);
        PokemonRarity.add("Nidoqueen", Rarity.DIAMOND);
        PokemonRarity.add("NidoranM", Rarity.COPPER);
        PokemonRarity.add("Nidorino", Rarity.SILVER);
        PokemonRarity.add("Nidoking", Rarity.DIAMOND);
        PokemonRarity.add("Clefairy", Rarity.SILVER);
        PokemonRarity.add("Clefable", Rarity.GOLD);
        PokemonRarity.add("Vulpix", Rarity.COPPER);
        PokemonRarity.add("Alolan Vulpix", Rarity.SILVER);
        PokemonRarity.add("Ninetales", Rarity.SILVER);
        PokemonRarity.add("Alolan Ninetales", Rarity.GOLD);
        PokemonRarity.add("Jigglypuff", Rarity.COPPER);
        PokemonRarity.add("Wigglytuff", Rarity.SILVER);
        PokemonRarity.add("Zubat", Rarity.COPPER);
        PokemonRarity.add("Golbat", Rarity.SILVER);
        PokemonRarity.add("Oddish", Rarity.COPPER);
        PokemonRarity.add("Gloom", Rarity.SILVER);
        PokemonRarity.add("Vileplume", Rarity.GOLD);
        PokemonRarity.add("Paras", Rarity.COPPER);
        PokemonRarity.add("Parasect", Rarity.SILVER);
        PokemonRarity.add("Venonat", Rarity.COPPER);
        PokemonRarity.add("Venomoth", Rarity.SILVER);
        PokemonRarity.add("Diglett", Rarity.COPPER);
        PokemonRarity.add("Alolan Diglett", Rarity.SILVER);
        PokemonRarity.add("Dugtrio", Rarity.SILVER);
        PokemonRarity.add("Alolan Dugtrio", Rarity.SILVER);

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
