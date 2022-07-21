package com.calculusmaster.pokecord.game.pokemon.augments;

import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.moves.MoveData;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.component.PokemonStats;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import org.jooq.lambda.tuple.Tuple2;

import java.util.*;

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

    //Incompatibilities
    public static final Map<PokemonAugment, EnumSet<PokemonAugment>> INCOMPATIBILITIES = Map.of(
            PokemonAugment.SUPERCHARGED, EnumSet.of(PokemonAugment.SUPERFORTIFIED),
            PokemonAugment.SUPERFORTIFIED, EnumSet.of(PokemonAugment.SUPERCHARGED),

            PokemonAugment.PRISMATIC_CONVERGENCE, EnumSet.of(PokemonAugment.RADIANT_DIFFRACTED_BEAMS),
            PokemonAugment.RADIANT_PRISMATIC_CONVERGENCE, EnumSet.of(PokemonAugment.RADIANT_DIFFRACTED_BEAMS)
    );

    public static boolean isIncompatibleWith(PokemonAugment augment, Collection<PokemonAugment> equipped)
    {
        return INCOMPATIBILITIES.containsKey(augment) && INCOMPATIBILITIES.get(augment).stream().anyMatch(equipped::contains);
    }

    //Augment Data
    public static final Map<String, PokemonAugmentData> AUGMENT_DATA = new LinkedHashMap<>();

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

        //Universal Augments
        PokemonData.POKEMON.forEach(pokemon -> {
            PokemonData data = PokemonData.get(pokemon);
            PokemonAugmentData augmentData = AUGMENT_DATA.get(pokemon);

            //XP Boost
            switch(data.growthRate)
            {
                case ERRATIC -> augmentData.registerAugment(25, PokemonAugment.XP_BOOST_I);
                case FAST -> augmentData.registerAugment(25, PokemonAugment.XP_BOOST_II);
                case MEDIUM_FAST, MEDIUM_SLOW -> augmentData.registerAugment(25, PokemonAugment.XP_BOOST_III);
                case SLOW -> augmentData.registerAugment(25, PokemonAugment.XP_BOOST_IV);
                case FLUCTUATING -> augmentData.registerAugment(25, PokemonAugment.XP_BOOST_V);
            }

            //EV Affinity
            augmentData.registerAugment(85, PokemonAugment.EV_AFFINITY);
        });

        //Somewhat Universal Augments
        PokemonData.POKEMON.forEach(s -> {
            PokemonData data = PokemonData.get(s);
            PokemonAugmentData augmentData = AUGMENT_DATA.get(s);

            PokemonRarity.Rarity rarity = PokemonRarity.POKEMON_RARITIES.getOrDefault(data.name.replaceAll("Primal|Mega|\\sY|\\sX", "").trim(), PokemonRarity.Rarity.EXTREME);

            //Supercharged & Superfortified
            if(List.of(PokemonRarity.Rarity.COPPER, PokemonRarity.Rarity.SILVER, PokemonRarity.Rarity.GOLD, PokemonRarity.Rarity.DIAMOND, PokemonRarity.Rarity.PLATINUM).contains(rarity))
                augmentData.registerAugment(35, PokemonAugment.SUPERCHARGED).registerAugment(35, PokemonAugment.SUPERFORTIFIED);

            //Harmony
            if(data.types.size() == 1) augmentData.registerAugment(40, PokemonAugment.HARMONY);

            //Pinnacle Evasion
            if(data.baseStats.get(Stat.SPD) >= 100) augmentData.registerAugment(56, PokemonAugment.PINNACLE_EVASION);

            //Precision Strikes
            if(data.baseStats.get(Stat.ATK) >= 90 && data.baseStats.get(Stat.SPD) <= 150) augmentData.registerAugment(60, PokemonAugment.PRECISION_STRIKES);

            //Precision Burst
            if(data.baseStats.get(Stat.ATK) >= 75) augmentData.registerAugment(65, PokemonAugment.PRECISION_BURST);

            //Raw & Modifying Force
            if(data.baseStats.get().values().stream().mapToInt(v -> v).sum() < 600) augmentData.registerAugment(65, PokemonAugment.RAW_FORCE).registerAugment(65, PokemonAugment.MODIFYING_FORCE);
        });

        //Move Augments
        PokemonData.POKEMON.forEach(s -> {
            PokemonData data = PokemonData.get(s);
            PokemonAugmentData augmentData = AUGMENT_DATA.get(s);

            //Weighted Punch
            if(data.moves.keySet().stream().anyMatch(move -> move.contains("Punch"))) augmentData.registerAugment(25, PokemonAugment.WEIGHTED_PUNCH);

            //Z-Affinity
            augmentData.registerAugment(100, PokemonAugment.Z_AFFINITY);

            //Restorative Hail
            if(data.moves.containsKey("Hail")) augmentData.registerAugment(52, PokemonAugment.RESTORATIVE_HAIL);

            //Restorative Sandstorm
            if(data.moves.containsKey("Sandstorm")) augmentData.registerAugment(52, PokemonAugment.RESTORATIVE_SANDSTORM);

            //Supercharged Tackle
            if(data.moves.containsKey("Tackle")) augmentData.registerAugment(15, PokemonAugment.SUPERCHARGED_TACKLE);

            //Meteor Mash
            if(data.moves.containsKey("Meteor Mash")) augmentData.registerAugment(50, PokemonAugment.METEOR_SHOWER);
        });

        //Marshadow
        AUGMENT_DATA.get("Marshadow").registerAugment(50, PokemonAugment.SPECTRAL_AMPLIFICATION).registerAugment(25, PokemonAugment.PHANTOM_TARGETING).registerAugment(35, PokemonAugment.SHADOW_PROPULSION).registerAugment(70, PokemonAugment.SPECTRAL_SUPERCHARGE);

        //Necrozma
        AUGMENT_DATA.get("Necrozma").registerAugment(60, PokemonAugment.PRISMATIC_CONVERGENCE).registerAugment(40, PokemonAugment.LIGHT_ABSORPTION).registerAugment(55, PokemonAugment.DIFFRACTED_BEAMS);
        AUGMENT_DATA.get("Dusk Mane Necrozma").registerAugment(60, PokemonAugment.PRISMATIC_CONVERGENCE).registerAugment(40, PokemonAugment.LIGHT_ABSORPTION).registerAugment(55, PokemonAugment.DIFFRACTED_BEAMS);
        AUGMENT_DATA.get("Dawn Wings Necrozma").registerAugment(60, PokemonAugment.PRISMATIC_CONVERGENCE).registerAugment(55, PokemonAugment.DIFFRACTED_BEAMS);
        AUGMENT_DATA.get("Ultra Necrozma").registerAugment(70, PokemonAugment.RADIANT_PRISMATIC_CONVERGENCE).registerAugment(30, PokemonAugment.LIGHT_ABSORPTION).registerAugment(65, PokemonAugment.RADIANT_DIFFRACTED_BEAMS);

        //Regieleki
        AUGMENT_DATA.get("Regieleki").registerAugment(50, PokemonAugment.ELECTRIFIED_HYPER_SPEED);
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

        public List<PokemonAugment> getOrderedAugmentList()
        {
            List<Integer> levels = this.augments.keySet().stream().sorted(Comparator.comparingInt(i -> i)).toList();

            List<PokemonAugment> list = new ArrayList<>();
            levels.forEach(level -> list.addAll(this.augments.get(level)));
            return list;
        }
    }
}
