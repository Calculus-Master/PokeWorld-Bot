package com.calculusmaster.pokecord.util;

import java.util.*;

public class PokemonRarity
{
    public static final List<String> SPAWNS = new ArrayList<>();
    public static final Map<String, Rarity> POKEMON_RARITIES = new HashMap<>();

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
        PokemonRarity.add("Meowth", Rarity.COPPER);
        PokemonRarity.add("Alolan Meowth", Rarity.SILVER);
        PokemonRarity.add("Galarian Meowth", Rarity.SILVER);
        PokemonRarity.add("Persian", Rarity.SILVER);
        PokemonRarity.add("Alolan Persian", Rarity.SILVER);
        PokemonRarity.add("Psyduck", Rarity.COPPER);
        PokemonRarity.add("Golduck", Rarity.SILVER);
        PokemonRarity.add("Mankey", Rarity.COPPER);
        PokemonRarity.add("Primeape", Rarity.SILVER);
        PokemonRarity.add("Growlithe", Rarity.SILVER);
        PokemonRarity.add("Arcanine", Rarity.GOLD);
        PokemonRarity.add("Poliwag", Rarity.COPPER);
        PokemonRarity.add("Poliwhirl", Rarity.SILVER);
        PokemonRarity.add("Poliwrath", Rarity.GOLD);
        PokemonRarity.add("Abra", Rarity.COPPER);
        PokemonRarity.add("Kadabra", Rarity.SILVER);
        PokemonRarity.add("Alakazam", Rarity.GOLD);
        PokemonRarity.add("Machop", Rarity.COPPER);
        PokemonRarity.add("Machoke", Rarity.SILVER);
        PokemonRarity.add("Machamp", Rarity.GOLD);
        PokemonRarity.add("Bellsprout", Rarity.COPPER);
        PokemonRarity.add("Weepinbell", Rarity.SILVER);
        PokemonRarity.add("Victreebel", Rarity.GOLD);
        PokemonRarity.add("Tentacool", Rarity.COPPER);
        PokemonRarity.add("Tentacruel", Rarity.SILVER);
        PokemonRarity.add("Geodude", Rarity.COPPER);
        PokemonRarity.add("Alolan Geodude", Rarity.SILVER);
        PokemonRarity.add("Graveler", Rarity.SILVER);
        PokemonRarity.add("Alolan Graveler", Rarity.GOLD);
        PokemonRarity.add("Golem", Rarity.GOLD);
        PokemonRarity.add("Alolan Golem", Rarity.DIAMOND);
        PokemonRarity.add("Ponyta", Rarity.COPPER);
        PokemonRarity.add("Galarian Ponyta", Rarity.SILVER);
        PokemonRarity.add("Rapidash", Rarity.SILVER);
        PokemonRarity.add("Galarian Rapidash", Rarity.SILVER);
        PokemonRarity.add("Slowpoke", Rarity.COPPER);
        PokemonRarity.add("Galarian Slowpoke", Rarity.SILVER);
        PokemonRarity.add("Slowbro", Rarity.SILVER);
        PokemonRarity.add("Galarian Slowbro", Rarity.SILVER);
        PokemonRarity.add("Magnemite", Rarity.COPPER);
        PokemonRarity.add("Magneton", Rarity.SILVER);
        PokemonRarity.add("Farfetchd", Rarity.SILVER);
        PokemonRarity.add("Galarian Farfetchd", Rarity.GOLD);
        PokemonRarity.add("Doduo", Rarity.COPPER);
        PokemonRarity.add("Dodrio", Rarity.SILVER);
        PokemonRarity.add("Seel", Rarity.COPPER);
        PokemonRarity.add("Dewgong", Rarity.SILVER);
        PokemonRarity.add("Grimer", Rarity.COPPER);
        PokemonRarity.add("Alolan Grimer", Rarity.SILVER);
        PokemonRarity.add("Muk", Rarity.SILVER);
        PokemonRarity.add("Alolan Muk", Rarity.SILVER);
        PokemonRarity.add("Shellder", Rarity.COPPER);
        PokemonRarity.add("Cloyster", Rarity.SILVER);
        PokemonRarity.add("Gastly", Rarity.COPPER);
        PokemonRarity.add("Haunter", Rarity.SILVER);
        PokemonRarity.add("Gengar", Rarity.GOLD);
        PokemonRarity.add("Onix", Rarity.COPPER);
        PokemonRarity.add("Drowzee", Rarity.COPPER);
        PokemonRarity.add("Hypno", Rarity.SILVER);
        PokemonRarity.add("Krabby", Rarity.COPPER);
        PokemonRarity.add("Kingler", Rarity.SILVER);
        PokemonRarity.add("Voltorb", Rarity.COPPER);
        PokemonRarity.add("Electrode", Rarity.SILVER);
        PokemonRarity.add("Exeggcute", Rarity.COPPER);
        PokemonRarity.add("Exeggutor", Rarity.SILVER);
        PokemonRarity.add("Alolan Exeggutor", Rarity.SILVER);

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

        POKEMON_RARITIES.put(name, r);
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
