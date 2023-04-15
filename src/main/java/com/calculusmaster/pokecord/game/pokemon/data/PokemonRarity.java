package com.calculusmaster.pokecord.game.pokemon.data;

import com.calculusmaster.pokecord.game.enums.elements.Region;
import com.calculusmaster.pokecord.util.Global;

import java.util.*;
import java.util.stream.IntStream;

public class PokemonRarity
{
    private static final Random r = new Random();

    private static final EnumSet<PokemonEntity> HISUIAN_POKEMON = EnumSet.of(PokemonEntity.WYRDEER, PokemonEntity.KLEAVOR, PokemonEntity.URSALUNA, PokemonEntity.BASCULEGION_MALE, PokemonEntity.BASCULEGION_FEMALE, PokemonEntity.SNEASLER, PokemonEntity.OVERQWIL, PokemonEntity.ENAMORUS);
    static { Arrays.stream(PokemonEntity.values()).filter(e -> !e.isNotSpawnable() && e.toString().contains("HISUI")).forEach(HISUIAN_POKEMON::add); }

    //Regular Spawn Weights, Excludes Non-Spawnables
    private static final List<PokemonEntity> DEFAULT_SPAWNS = new ArrayList<>();
    //Region-Based Spawn Weights, Excludes Non-Spawnables
    private static final List<PokemonEntity> CURRENT_SPAWNS = new ArrayList<>();

    //Regular Spawn Weights, Includes Non-Spawnables
    private static final List<PokemonEntity> DEFAULT_POKEMON = new ArrayList<>();
    //Region-Based Spawn Weights, Includes Non-Spawnables
    private static final List<PokemonEntity> CURRENT_POKEMON = new ArrayList<>();

    public static float REGION_SPAWN_RATE_BOOST = 1.25F;

    //Classifiers
    public static boolean isLegendary(PokemonEntity p)
    {
        return p.getRarity() == Rarity.LEGENDARY;
    }

    public static boolean isMythical(PokemonEntity p)
    {
        return p.getRarity() == Rarity.MYTHICAL;
    }

    public static boolean isUltraBeast(PokemonEntity p)
    {
        return p.getRarity() == Rarity.ULTRA_BEAST;
    }

    //Updating Spawns based on World Region
    public static void updateSpawnWeights(Region region)
    {
        CURRENT_POKEMON.clear();
        CURRENT_SPAWNS.clear();

        Arrays.stream(PokemonEntity.values()).forEach(e -> {
            int weight = e.getRarity().weight;

            //Region-Specific Pokemon Boost
            if(region == Region.HISUI && HISUIAN_POKEMON.contains(e)) weight *= REGION_SPAWN_RATE_BOOST;
            else if(region != Region.HISUI && region.getGeneration() == e.getGeneration()) weight *= REGION_SPAWN_RATE_BOOST;

            IntStream.range(0, weight).forEach(i -> CURRENT_POKEMON.add(e));
        });

        CURRENT_POKEMON.stream().filter(p -> !p.isNotSpawnable()).forEach(CURRENT_SPAWNS::add);
    }

    //Rarity Methods

    public static void init()
    {
        //General Weighted Pokemon List (+ copy into Current)
        Arrays.stream(PokemonEntity.values()).forEach(e -> IntStream.range(0, e.getRarity().weight).forEach(i -> DEFAULT_POKEMON.add(e)));
        CURRENT_POKEMON.addAll(DEFAULT_POKEMON);

        //Filter from Default to get Spawn List
        DEFAULT_POKEMON.stream().filter(p -> !p.isNotSpawnable()).forEach(DEFAULT_SPAWNS::add);
        CURRENT_SPAWNS.addAll(DEFAULT_SPAWNS);
    }

    //Accessors
    public static PokemonEntity getSpawn(boolean isDefault)
    {
        List<PokemonEntity> l = isDefault ? DEFAULT_SPAWNS : CURRENT_SPAWNS;

        return l.get(r.nextInt(l.size()));
    }

    public static PokemonEntity getSpawn()
    {
        return PokemonRarity.getSpawn(false);
    }

    public static PokemonEntity getPokemon(boolean isDefault)
    {
        List<PokemonEntity> l = isDefault ? DEFAULT_POKEMON : CURRENT_POKEMON;

        return l.get(r.nextInt(l.size()));
    }

    public static PokemonEntity getPokemon()
    {
        return PokemonRarity.getPokemon(false);
    }

    public static PokemonEntity getSpawn(boolean isDefault, Rarity... rarities)
    {
        EnumSet<Rarity> rarityList = EnumSet.copyOf(List.of(rarities));
        List<PokemonEntity> l = (isDefault ? DEFAULT_SPAWNS : CURRENT_SPAWNS).stream().filter(p -> rarityList.contains(p.getRarity())).toList();

        return l.get(r.nextInt(l.size()));
    }

    public static PokemonEntity getPokemon(boolean isDefault, Rarity... rarities)
    {
        EnumSet<Rarity> rarityList = EnumSet.copyOf(List.of(rarities));
        List<PokemonEntity> l = (isDefault ? DEFAULT_POKEMON : CURRENT_POKEMON).stream().filter(p -> rarityList.contains(p.getRarity())).toList();

        return l.get(r.nextInt(l.size()));
    }

    public enum Rarity
    {
        COPPER(100),
        SILVER(75),
        GOLD(50),
        DIAMOND(25),
        PLATINUM(15),
        MYTHICAL(10),
        ULTRA_BEAST(8),
        LEGENDARY(5),

        ;

        public final int weight;
        Rarity(int weight)
        {
            this.weight = weight;
        }

        public static Rarity cast(String input)
        {
            return Global.getEnumFromString(values(), input);
        }
    }
}
