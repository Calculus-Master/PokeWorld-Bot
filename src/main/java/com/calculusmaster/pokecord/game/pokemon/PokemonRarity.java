package com.calculusmaster.pokecord.game.pokemon;

import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.CSVHelper;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;

import java.util.*;

public class PokemonRarity
{
    public static final List<String> SPAWNS = new ArrayList<>();
    public static final LinkedHashMap<String, Rarity> POKEMON_RARITIES = new LinkedHashMap<>();

    public static final List<String> LEGENDARY = Arrays.asList("Articuno", "Galarian Articuno", "Moltres", "Galarian Moltres", "Zapdos", "Galarian Zapdos", "Mewtwo", "Mega Mewtwo X", "Mega Mewtwo Y", "Raikou", "Entei", "Suicune", "Lugia", "Ho Oh", "Regirock", "Regice", "Registeel", "Latias", "Mega Latias", "Latios", "Mega Latios", "Kyogre", "Primal Kyogre", "Groudon", "Primal Groudon", "Rayquaza", "Mega Rayquaza", "Deoxys", "Deoxys Attack", "Deoxys Defense", "Deoxys Speed", "Dialga", "Palkia", "Heatran", "Regigigas", "Giratina", "Origin Giratina", "Cresselia", "Darkrai", "Arceus", "Cobalion", "Terrakion", "Virizion", "Tornadus", "Thundurus", "Zekrom", "Reshiram", "Landorus", "Kyurem", "Black Kyurem", "White Kyurem", "Keldeo", "Xerneas", "Yveltal", "Zygarde", "Zygarde 10", "Zygarde Complete", "Volcanion", "Solgaleo", "Lunala", "Necrozma", "Dusk Mane Necrozma", "Dawn Wings Necrozma", "Ultra Necrozma", "Meltan", "Melmetal", "Zacian", "Zamazenta", "Eternatus", "Regieleki", "Regidrago", "Glastrier", "Spectrier", "Calyrex", "Ice Rider Calyrex", "Shadow Rider Calyrex");
    public static final List<String> MYTHICAL = Arrays.asList("Mew", "Celebi", "Jirachi", "Uxie", "Mesprit", "Azelf", "Phione", "Manaphy", "Shaymin", "Shaymin Sky", "Victini", "Meloetta Pirouette", "Genesect", "Diancie", "Mega Diancie", "Hoopa", "Hoopa Unbound", "Tapu Koko", "Tapu Lele", "Tapu Bulu", "Tapu Fini", "Magearna", "Marshadow", "Zeraora", "Zarude");
    public static final List<String> ULTRA_BEAST = Arrays.asList("Nihilego", "Buzzwole", "Pheromosa", "Xurkitree", "Celesteela", "Kartana", "Guzzlord", "Poipole", "Naganadel", "Stakataka", "Blacephalon");
    public static final List<String> MEGA = Arrays.asList("Mega Venusaur", "Mega Charizard X", "Mega Charizard Y", "Mega Blastoise", "Mega Alakazam", "Mega Gengar", "Mega Kangaskhan", "Mega Pinsir", "Mega Gyarados", "Mega Aerodactyl", "Mega Ampharos", "Mega Scizor", "Mega Heracross", "Mega Houndoom", "Mega Tyranitar", "Mega Blaziken", "Mega Gardevoir", "Mega Mawile", "Mega Aggron", "Mega Medicham", "Mega Manectric", "Mega Banette", "Mega Absol", "Mega Garchomp", "Mega Lucario", "Mega Abomasnow", "Mega Beedrill", "Mega Pidgeot", "Mega Slowbro", "Mega Steelix", "Mega Sceptile", "Mega Swampert", "Mega Sableye", "Mega Sharpedo", "Mega Camerupt", "Mega Altaria", "Mega Glalie", "Mega Salamence", "Mega Lopunny", "Mega Gallade", "Mega Audino", "Mega Metagross");

    public static void init()
    {
        CSVHelper.CSV_POKEMON_DATA_RARITIES.forEach(line -> PokemonRarity.add(line[0], Rarity.cast(line[1])));

        Collections.shuffle(SPAWNS);

        LoggerHelper.info(PokemonRarity.class, "Spawn List Size: " + SPAWNS.size());
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

        String spawn = combined.get(new Random().nextInt(combined.size()));

        return SPAWNS.contains(spawn) ? spawn : getLegendarySpawn();
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

        public static Rarity cast(String input)
        {
            return Global.getEnumFromString(values(), input);
        }
    }
}
