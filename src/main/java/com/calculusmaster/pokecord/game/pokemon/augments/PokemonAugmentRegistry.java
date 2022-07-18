package com.calculusmaster.pokecord.game.pokemon.augments;

import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.moves.MoveData;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.component.PokemonStats;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.helpers.CSVHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.jooq.lambda.tuple.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

public class PokemonAugmentRegistry
{
    //Augment Slots
    public static final Map<PokemonRarity.Rarity, List<Integer>> AUGMENT_SLOTS = Map.of(
            PokemonRarity.Rarity.COPPER,    List.of(10, 15, 20, 25, 30, 35, 40, 50, 90),
            PokemonRarity.Rarity.SILVER,    List.of(10, 15, 25, 30, 40, 45, 55, 60, 90),
            PokemonRarity.Rarity.GOLD,      List.of(10, 30, 40, 50, 60, 70, 80, 90),
            PokemonRarity.Rarity.DIAMOND,   List.of(10, 25, 35, 45, 55, 75, 90),
            PokemonRarity.Rarity.PLATINUM,  List.of(10, 25, 40, 50, 80, 90),
            PokemonRarity.Rarity.MYTHICAL,  List.of(10, 30, 60, 90),
            PokemonRarity.Rarity.LEGENDARY, List.of(10, 50, 90),
            PokemonRarity.Rarity.EXTREME,   List.of(10, 50, 90)
    );

    //Augment Data
    public static final Map<String, PokemonAugmentData> AUGMENT_DATA = new LinkedHashMap<>();

    public static void main(String[] args)
    {
        CSVHelper.init();
        MoveData.init();
        PokemonData.init();
        init();

        AUGMENT_DATA.forEach((s, d) -> System.out.println(s + ": " + d.augments.entrySet().stream().map(e -> "(" + e.getKey() + " – " + e.getValue().stream().map(a -> a.toString()).collect(Collectors.joining(", ")) + ")").collect(Collectors.joining("   |   "))));

        Mongo.PlayerData.updateMany(Filters.exists("playerID"), Updates.set("owned_augments", EnumSet.noneOf(PokemonAugment.class)));
    }

    public static void init()
    {
        //Automatic registry of Basic Stat Boost Augments
        PokemonData.POKEMON.forEach(pokemon -> {
            PokemonData data = PokemonData.get(pokemon);
            PokemonStats baseStats = data.baseStats;

            double average = Arrays.stream(Stat.values()).mapToInt(baseStats::get).average().orElse(0.0);

            boolean physicalFocus = data.moves.keySet().stream().map(MoveData::get).filter(m -> m.category.equals(Category.PHYSICAL)).count() > data.moves.keySet().stream().map(MoveData::get).filter(m -> m.category.equals(Category.SPECIAL)).count();

            List<Tuple2<PokemonAugment, Integer>> augments = new ArrayList<>();
            for(Stat stat : Stat.values())
            {
                double ratio = baseStats.get(stat) / average;

                double significantPercentChange = 0.3;
                int tier = ratio < (1 - significantPercentChange) ? 3 : (ratio > (1 + significantPercentChange) ? 1 : 2);

                PokemonAugment boostAugment;
                if(tier == 1) boostAugment = switch(stat) {
                    case HP -> PokemonAugment.HP_BOOST_I;
                    case ATK -> PokemonAugment.ATK_BOOST_I;
                    case DEF -> PokemonAugment.DEF_BOOST_I;
                    case SPATK -> PokemonAugment.SPATK_BOOST_I;
                    case SPDEF -> PokemonAugment.SPDEF_BOOST_I;
                    case SPD -> PokemonAugment.SPD_BOOST_I;
                };
                else if(tier == 2) boostAugment = switch(stat) {
                    case HP -> PokemonAugment.HP_BOOST_II;
                    case ATK -> PokemonAugment.ATK_BOOST_II;
                    case DEF -> PokemonAugment.DEF_BOOST_II;
                    case SPATK -> PokemonAugment.SPATK_BOOST_II;
                    case SPDEF -> PokemonAugment.SPDEF_BOOST_II;
                    case SPD -> PokemonAugment.SPD_BOOST_II;
                };
                else boostAugment = switch(stat) {
                    case HP -> PokemonAugment.HP_BOOST_III;
                    case ATK -> PokemonAugment.ATK_BOOST_III;
                    case DEF -> PokemonAugment.DEF_BOOST_III;
                    case SPATK -> PokemonAugment.SPATK_BOOST_III;
                    case SPDEF -> PokemonAugment.SPDEF_BOOST_III;
                    case SPD -> PokemonAugment.SPD_BOOST_III;
                };

                int level;
                if(stat.equals(Stat.HP) || stat.equals(Stat.SPD)) level = tier == 1 ? 10 : (tier == 2 ? 25 : 40);
                else level = tier == 1 ? 25 : (tier == 2 ? 35 : 50);

                if((physicalFocus && (stat.equals(Stat.ATK) || stat.equals(Stat.DEF))) ||
                        (!physicalFocus && (stat.equals(Stat.SPATK) || stat.equals(Stat.SPDEF)))) level -= 8;

                augments.add(new Tuple2<>(boostAugment, level));
             }

            PokemonAugmentData augmentData = PokemonAugmentRegistry.register(pokemon);
            augments.forEach(t -> augmentData.registerAugment(t.v2, t.v1));
            augmentData.build();
        });
    }

    private static PokemonAugmentData register(String pokemon)
    {
        PokemonAugmentData data = new PokemonAugmentData();
        data.pokemon = pokemon;
        data.augments = new HashMap<>();
        return data;
    }

    public static class PokemonAugmentData
    {
        private String pokemon;
        private Map<Integer, EnumSet<PokemonAugment>> augments;

        private PokemonAugmentData registerAugment(int level, PokemonAugment augment)
        {
            if(!this.augments.containsKey(level)) this.augments.put(level, EnumSet.noneOf(PokemonAugment.class));
            this.augments.get(level).add(augment);
            return this;
        }

        private void build()
        {
            AUGMENT_DATA.put(this.pokemon, this);
        }

        public boolean has(PokemonAugment augment)
        {
            return this.augments.values().stream().anyMatch(list -> list.contains(augment));
        }

        public Map<Integer, EnumSet<PokemonAugment>> getAugmentsInfo()
        {
            return this.augments;
        }
    }
}
