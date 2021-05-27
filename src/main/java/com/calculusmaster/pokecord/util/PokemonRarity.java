package com.calculusmaster.pokecord.util;

import java.util.*;

public class PokemonRarity
{
    public static final List<String> SPAWNS = new ArrayList<>();
    public static final Map<String, Rarity> POKEMON_RARITIES = new HashMap<>();

    public static final List<String> LEGENDARY = Arrays.asList("Articuno", "Galarian Articuno", "Moltres", "Galarian Moltres", "Zapdos", "Galarian Zapdos", "Mewtwo", "Mega Mewtwo X", "Mega Mewtwo Y", "Raikou", "Entei", "Suicune", "Lugia", "Ho-Oh", "Regirock", "Regice", "Registeel", "Latias", "Mega Latias", "Latios", "Mega Latios", "Kyogre", "Primal Kyogre", "Groudon", "Primal Groudon", "Rayquaza", "Mega Rayquaza", "Deoxys", "Deoxys Attack", "Deoxys Defense", "Deoxys Speed", "Dialga", "Palkia", "Heatran", "Regigigas", "Giratina", "Origin Giratina", "Cresselia", "Darkrai", "Arceus", "Cobalion", "Terrakion", "Virizion", "Tornadus", "Thundurus", "Zekrom", "Reshiram", "Landorus", "Kyurem", "Black Kyurem", "White Kyurem", "Keldeo", "Xerneas", "Yveltal", "Zygarde", "Zygarde 10", "Zygarde Complete", "Volcanion", "Tapu Koko", "Tapu Lele", "Tapu Bulu", "Tapu Fini", "Solgaleo", "Lunala", "Necrozma", "Dusk Mane Necrozma", "Dawn Wings Necrozma", "Ultra Necrozma", "Meltan", "Melmetal", "Zacian", "Zamazenta", "Eternatus", "Regieleki", "Regidrago", "Glastrier", "Spectrier", "Calyrex", "Ice Rider Calyrex", "Shadow Rider Calyrex");
    public static final List<String> MYTHICAL = Arrays.asList("Mew", "Celebi", "Jirachi", "Uxie", "Mespirit", "Azelf", "Phione", "Manaphy", "Shaymin", "Shaymin Sky", "Meloetta Pirouette", "Genesect", "Diancie", "Mega Diancie", "Hoopa", "Hoopa Unbound", "Magearna", "Marshadow", "Zeraora", "Zarude");
    public static final List<String> ULTRA_BEAST = Arrays.asList("Nihilego", "Buzzwole", "Pheromosa", "Xurkitree", "Celesteela", "Kartana", "Guzzlord", "Poipole", "Naganadel", "Stakataka", "Blacephalon");
    public static final List<String> MEGA = Arrays.asList("Mega Venusaur", "Mega Charizard X", "Mega Charizard Y", "Mega Blastoise", "Mega Alakazam", "Mega Gengar", "Mega Kangaskhan", "Mega Pinsir", "Mega Gyarados", "Mega Aerodactyl", "Mega Ampharos", "Mega Scizor", "Mega Heracross", "Mega Houndoom", "Mega Tyranitar", "Mega Blaziken", "Mega Gardevoir", "Mega Mawile", "Mega Aggron", "Mega Medicham", "Mega Manectric", "Mega Banette", "Mega Absol", "Mega Garchomp", "Mega Lucario", "Mega Abomasnow", "Mega Beedrill", "Mega Pidgeot", "Mega Slowbro", "Mega Steelix", "Mega Sceptile", "Mega Swampert", "Mega Sableye", "Mega Sharpedo", "Mega Camerupt", "Mega Altaria", "Mega Glalie", "Mega Salamence", "Mega Lopunny", "Mega Gallade", "Mega Audino");

    public static void init()
    {

        //Gen 1

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
        PokemonRarity.add("Cubone", Rarity.COPPER);
        PokemonRarity.add("Marowak", Rarity.SILVER);
        PokemonRarity.add("Alolan Marowak", Rarity.SILVER);
        PokemonRarity.add("Hitmonlee", Rarity.SILVER);
        PokemonRarity.add("Hitmonchan", Rarity.SILVER);
        PokemonRarity.add("Lickitung", Rarity.SILVER);
        PokemonRarity.add("Koffing", Rarity.COPPER);
        PokemonRarity.add("Weezing", Rarity.SILVER);
        PokemonRarity.add("Galarian Weezing", Rarity.SILVER);
        PokemonRarity.add("Rhyhorn", Rarity.COPPER);
        PokemonRarity.add("Rhydon", Rarity.SILVER);
        PokemonRarity.add("Chansey", Rarity.SILVER);
        PokemonRarity.add("Tangela", Rarity.SILVER);
        PokemonRarity.add("Kangaskhan", Rarity.GOLD);
        PokemonRarity.add("Horsea", Rarity.COPPER);
        PokemonRarity.add("Seadra", Rarity.SILVER);
        PokemonRarity.add("Goldeen", Rarity.COPPER);
        PokemonRarity.add("Seaking", Rarity.SILVER);
        PokemonRarity.add("Staryu", Rarity.COPPER);
        PokemonRarity.add("Starmie", Rarity.SILVER);
        PokemonRarity.add("Mr Mime", Rarity.COPPER);
        PokemonRarity.add("Galarian MrMime", Rarity.SILVER);
        PokemonRarity.add("Scyther", Rarity.COPPER);
        PokemonRarity.add("Jynx", Rarity.SILVER);
        PokemonRarity.add("Electabuzz", Rarity.SILVER);
        PokemonRarity.add("Magmar", Rarity.SILVER);
        PokemonRarity.add("Pinsir", Rarity.SILVER);
        PokemonRarity.add("Tauros", Rarity.SILVER);
        PokemonRarity.add("Magikarp", Rarity.COPPER);
        PokemonRarity.add("Gyarados", Rarity.SILVER);
        PokemonRarity.add("Lapras", Rarity.SILVER);
        PokemonRarity.add("Ditto", Rarity.SILVER);
        PokemonRarity.add("Eevee", Rarity.COPPER);
        PokemonRarity.add("Vaporeon", Rarity.SILVER);
        PokemonRarity.add("Jolteon", Rarity.SILVER);
        PokemonRarity.add("Flareon", Rarity.SILVER);
        PokemonRarity.add("Porygon", Rarity.COPPER);
        PokemonRarity.add("Omanyte", Rarity.SILVER);
        PokemonRarity.add("Omastar", Rarity.GOLD);
        PokemonRarity.add("Kabuto", Rarity.SILVER);
        PokemonRarity.add("Kabutops", Rarity.GOLD);
        PokemonRarity.add("Aerodactyl", Rarity.SILVER);
        PokemonRarity.add("Snorlax", Rarity.SILVER);
        PokemonRarity.add("Articuno", Rarity.LEGENDARY);
        PokemonRarity.add("Galarian Articuno", Rarity.LEGENDARY);
        PokemonRarity.add("Zapdos", Rarity.LEGENDARY);
        PokemonRarity.add("Galarian Zapdos", Rarity.LEGENDARY);
        PokemonRarity.add("Moltres", Rarity.LEGENDARY);
        PokemonRarity.add("Galarian Moltres", Rarity.LEGENDARY);
        PokemonRarity.add("Dratini", Rarity.COPPER);
        PokemonRarity.add("Dragonair", Rarity.SILVER);
        PokemonRarity.add("Dragonite", Rarity.GOLD);
        PokemonRarity.add("Mewtwo", Rarity.LEGENDARY);
        PokemonRarity.add("Mew", Rarity.MYTHICAL);

        //Gen 2

        PokemonRarity.add("Chikorita", Rarity.SILVER);
        PokemonRarity.add("Bayleef", Rarity.GOLD);
        PokemonRarity.add("Meganium", Rarity.DIAMOND);
        PokemonRarity.add("Cyndaquil", Rarity.SILVER);
        PokemonRarity.add("Quilava", Rarity.GOLD);
        PokemonRarity.add("Typhlosion", Rarity.DIAMOND);
        PokemonRarity.add("Totodile", Rarity.SILVER);
        PokemonRarity.add("Croconaw", Rarity.GOLD);
        PokemonRarity.add("Feraligatr", Rarity.DIAMOND);

        PokemonRarity.add("Steelix", Rarity.GOLD);

        //Gen 3

        PokemonRarity.add("Treecko", Rarity.SILVER);
        PokemonRarity.add("Grovyle", Rarity.GOLD);
        PokemonRarity.add("Sceptile", Rarity.DIAMOND);
        PokemonRarity.add("Torchic", Rarity.SILVER);
        PokemonRarity.add("Combusken", Rarity.GOLD);
        PokemonRarity.add("Blaziken", Rarity.DIAMOND);
        PokemonRarity.add("Mudkip", Rarity.SILVER);
        PokemonRarity.add("Marshtomp", Rarity.GOLD);
        PokemonRarity.add("Swampert", Rarity.DIAMOND);

        PokemonRarity.add("Regirock", Rarity.LEGENDARY);
        PokemonRarity.add("Regice", Rarity.LEGENDARY);
        PokemonRarity.add("Registeel", Rarity.LEGENDARY);

        PokemonRarity.add("Groudon", Rarity.LEGENDARY);
        PokemonRarity.add("Kyogre", Rarity.LEGENDARY);
        PokemonRarity.add("Rayquaza", Rarity.LEGENDARY);

        //Gen 4

        PokemonRarity.add("Dialga", Rarity.LEGENDARY);
        PokemonRarity.add("Palkia", Rarity.LEGENDARY);

        PokemonRarity.add("Regigigas", Rarity.LEGENDARY);
        PokemonRarity.add("Giratina", Rarity.LEGENDARY);
        PokemonRarity.add("Origin Giratina", Rarity.EXTREME);

        //Gen 5

        PokemonRarity.add("Victini", Rarity.MYTHICAL);

        PokemonRarity.add("Tornadus", Rarity.LEGENDARY);
        PokemonRarity.add("Tornadus Therian", Rarity.EXTREME);
        PokemonRarity.add("Thundurus", Rarity.LEGENDARY);
        PokemonRarity.add("Thundurus Therian", Rarity.EXTREME);

        PokemonRarity.add("Landorus", Rarity.LEGENDARY);
        PokemonRarity.add("Landorus Therian", Rarity.EXTREME);

        //Gen 6

        //Gen 7

        PokemonRarity.add("Grubbin", Rarity.COPPER);
        PokemonRarity.add("Charjabug", Rarity.SILVER);
        PokemonRarity.add("Vikavolt", Rarity.GOLD);

        PokemonRarity.add("Necrozma", Rarity.LEGENDARY);
        PokemonRarity.add("Dusk Mane Necrozma", Rarity.EXTREME);
        PokemonRarity.add("Dawn Wings Necrozma", Rarity.EXTREME);

        PokemonRarity.add("Marshadow", Rarity.MYTHICAL);

        PokemonRarity.add("Meltan", Rarity.PLATINUM);
        PokemonRarity.add("Melmetal", Rarity.MYTHICAL);

        //Gen 8

        PokemonRarity.add("Eternatus", Rarity.LEGENDARY);

        PokemonRarity.add("Regieleki", Rarity.LEGENDARY);
        PokemonRarity.add("Regidrago", Rarity.LEGENDARY);

        Collections.shuffle(SPAWNS);

        System.out.println("Spawn Master List Size: " + SPAWNS.size());
        //System.out.println(SPAWNS.stream().map(n -> n.substring(0, 2)).toString());
    }

    public static String getSpawn()
    {
        return SPAWNS.get(new Random().nextInt(SPAWNS.size()));
    }

    public static String getLegendarySpawn()
    {
        List<String> combined = new ArrayList<>();

        combined.addAll(LEGENDARY);
        combined.addAll(MYTHICAL);
        combined.addAll(ULTRA_BEAST);

        return combined.get(new Random().nextInt(combined.size()));
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
        LEGENDARY(5),
        EXTREME(1);

        public int num;
        Rarity(int num)
        {
            this.num = num;
        }
    }
}
