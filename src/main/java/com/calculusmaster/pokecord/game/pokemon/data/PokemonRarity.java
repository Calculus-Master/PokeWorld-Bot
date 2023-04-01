package com.calculusmaster.pokecord.game.pokemon.data;

import com.calculusmaster.pokecord.util.Global;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class PokemonRarity
{
    public static final List<PokemonEntity> SPAWNS = new ArrayList<>();

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

    //Rarity Methods

    public static void init()
    {
        Arrays.stream(PokemonEntity.values()).filter(p -> !p.isNotSpawnable()).forEach(e -> IntStream.range(0, e.getRarity().num).forEach(i -> SPAWNS.add(e)));
        Collections.shuffle(SPAWNS);
    }

    public static PokemonEntity getSpawn()
    {
        return SPAWNS.get(new Random().nextInt(SPAWNS.size()));
    }

    public static PokemonEntity getSpawn(Predicate<PokemonEntity> filter)
    {
        List<PokemonEntity> filteredSpawns = SPAWNS.stream().filter(filter).toList();

        return filteredSpawns.get(new Random().nextInt(filteredSpawns.size()));
    }

    public static PokemonEntity getLegendarySpawn()
    {
        return PokemonRarity.getSpawn(PokemonRarity::isLegendary);
    }

    public static PokemonEntity getSpawnOfRarities(Rarity... rarities)
    {
        return PokemonRarity.getSpawn(e -> List.of(rarities).contains(e.getRarity()));
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

        public final int num;
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
