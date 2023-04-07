package com.calculusmaster.pokecord.game.pokemon.data;

import com.calculusmaster.pokecord.game.enums.elements.Region;
import com.calculusmaster.pokecord.util.Global;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class PokemonRarity
{
    private static final Random r = new Random();

    private static final List<PokemonEntity> DEFAULT_SPAWN_LIST = new ArrayList<>();
    private static final List<PokemonEntity> CURRENT_SPAWN_LIST = new ArrayList<>();

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
        final EnumSet<PokemonEntity> HISUIAN_POKEMON = EnumSet.of(PokemonEntity.WYRDEER, PokemonEntity.KLEAVOR, PokemonEntity.URSALUNA, PokemonEntity.BASCULEGION_MALE, PokemonEntity.BASCULEGION_FEMALE, PokemonEntity.SNEASLER, PokemonEntity.OVERQWIL, PokemonEntity.ENAMORUS);
        Arrays.stream(PokemonEntity.values()).filter(e -> e.isNotSpawnable() && e.toString().contains("HISUI")).forEach(HISUIAN_POKEMON::add);

        CURRENT_SPAWN_LIST.clear();
        Arrays.stream(PokemonEntity.values()).filter(e -> !e.isNotSpawnable()).forEach(e -> {
            int weight = e.getRarity().weight;

            //Region-Specific Pokemon Boost
            if(region == Region.HISUI && HISUIAN_POKEMON.contains(e)) weight *= REGION_SPAWN_RATE_BOOST;
            else if(region != Region.HISUI && region.getGeneration() == e.getGeneration()) weight *= REGION_SPAWN_RATE_BOOST;

            IntStream.range(0, weight).forEach(i -> CURRENT_SPAWN_LIST.add(e));
        });
    }

    //Rarity Methods

    public static void init()
    {
        Arrays.stream(PokemonEntity.values()).filter(p -> !p.isNotSpawnable()).forEach(e -> IntStream.range(0, e.getRarity().weight).forEach(i -> DEFAULT_SPAWN_LIST.add(e)));
        CURRENT_SPAWN_LIST.addAll(DEFAULT_SPAWN_LIST);
    }

    public static PokemonEntity getSpawn(boolean useDefaultSpawnList)
    {
        List<PokemonEntity> list = useDefaultSpawnList ? DEFAULT_SPAWN_LIST : CURRENT_SPAWN_LIST;
        return list.get(r.nextInt(list.size()));
    }

    public static PokemonEntity getSpawn()
    {
        return PokemonRarity.getSpawn(false);
    }

    public static PokemonEntity getSpawn(boolean isDefault, Predicate<PokemonEntity> filter)
    {
        List<PokemonEntity> filteredSpawns = (isDefault ? DEFAULT_SPAWN_LIST : CURRENT_SPAWN_LIST).stream().filter(filter).toList();

        return filteredSpawns.get(r.nextInt(filteredSpawns.size()));
    }

    public static PokemonEntity getSpawn(boolean isDefault, Rarity... rarities)
    {
        return PokemonRarity.getSpawn(isDefault, e -> List.of(rarities).contains(e.getRarity()));
    }

    public static PokemonEntity getLegendarySpawn()
    {
        return PokemonRarity.getSpawn(false, PokemonRarity::isLegendary);
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
