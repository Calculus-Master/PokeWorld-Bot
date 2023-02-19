package com.calculusmaster.pokecord.game.pokemon.augments;

import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;

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

    public static final int MAX_AUGMENTS = 10;

    //Incompatibilities
    public static final List<EnumSet<PokemonAugment>> INCOMPATIBILITIES = List.of(
            EnumSet.of(PokemonAugment.AERIAL_EVASION, PokemonAugment.PINNACLE_EVASION),

            EnumSet.of(PokemonAugment.SUPERFORTIFIED, PokemonAugment.SUPERCHARGED),

            EnumSet.of(PokemonAugment.PRISMATIC_CONVERGENCE, PokemonAugment.RADIANT_PRISMATIC_CONVERGENCE, PokemonAugment.DIFFRACTED_BEAMS, PokemonAugment.RADIANT_DIFFRACTED_BEAMS)
    );

    public static boolean isIncompatibleWith(PokemonAugment augment, Collection<PokemonAugment> equipped)
    {
        boolean incompatible = false;
        for(EnumSet<PokemonAugment> set : INCOMPATIBILITIES) if(set.contains(augment)) incompatible = incompatible || equipped.stream().anyMatch(set::contains);
        return incompatible;
    }

    //Augment Data
    public static final Map<String, PokemonAugmentData> AUGMENT_DATA = new LinkedHashMap<>();

    public static void init()
    {
        PokemonData.POKEMON.stream().map(PokemonData::get).forEach(pokemon -> {
            PokemonAugmentData data = PokemonAugmentRegistry.register(pokemon.name);
            PokemonRarity.Rarity rarity = PokemonRarity.POKEMON_RARITIES.getOrDefault(pokemon.name, PokemonRarity.Rarity.EXTREME);

            //Unique Augments

            if("Necrozma".equals(pokemon.name))
            {
                data.registerAugment(40, PokemonAugment.LIGHT_ABSORPTION);
                data.registerAugment(55, PokemonAugment.DIFFRACTED_BEAMS);
                data.registerAugment(60, PokemonAugment.PRISMATIC_CONVERGENCE);
            }
            else if("Dusk Mane Necrozma".equals(pokemon.name))
            {
                data.registerAugment(40, PokemonAugment.LIGHT_ABSORPTION);
                data.registerAugment(55, PokemonAugment.DIFFRACTED_BEAMS);
                data.registerAugment(60, PokemonAugment.PRISMATIC_CONVERGENCE);
            }
            else if("Dawn Wings Necrozma".contains(pokemon.name))
            {
                data.registerAugment(40, PokemonAugment.PRISMATIC_MOONLIT_SHIELD);
                data.registerAugment(55, PokemonAugment.DIFFRACTED_BEAMS);
                data.registerAugment(60, PokemonAugment.PRISMATIC_CONVERGENCE);
            }
            else if("Ultra Necrozma".contains(pokemon.name))
            {
                data.registerAugment(30, PokemonAugment.LIGHT_ABSORPTION);
                data.registerAugment(65, PokemonAugment.RADIANT_DIFFRACTED_BEAMS);
                data.registerAugment(70, PokemonAugment.RADIANT_PRISMATIC_CONVERGENCE);
            }
            else if("Marshadow".contains(pokemon.name))
            {
                data.registerAugment(25, PokemonAugment.PHANTOM_TARGETING);
                data.registerAugment(35, PokemonAugment.SHADOW_PROPULSION);
                data.registerAugment(50, PokemonAugment.SPECTRAL_AMPLIFICATION);
                data.registerAugment(70, PokemonAugment.SPECTRAL_SUPERCHARGE);
            }
            else if("Regieleki".contains(pokemon.name))
            {
                data.registerAugment(50, PokemonAugment.ELECTRIFIED_HYPER_SPEED);
            }
            else if("Victini".contains(pokemon.name))
            {
                data.registerAugment(30, PokemonAugment.FINAL_RESORT_V);
                data.registerAugment(40, PokemonAugment.V_RUSH);
                data.registerAugment(65, PokemonAugment.VICTORY_RESOLVE);
                data.registerAugment(80, PokemonAugment.SHINING_STAR);
                data.registerAugment(90, PokemonAugment.VICTORY_ENSURED);
            }

            //Move Augments

            if(pokemon.moves.keySet().stream().anyMatch(move -> move.contains("Punch")))
                data.registerAugment(25, PokemonAugment.WEIGHTED_PUNCH);

            if(!List.of(PokemonRarity.Rarity.MYTHICAL, PokemonRarity.Rarity.LEGENDARY, PokemonRarity.Rarity.EXTREME).contains(rarity))
                data.registerAugment(100, PokemonAugment.Z_AFFINITY);

            if(pokemon.moves.containsKey("Hail"))
                data.registerAugment(52, PokemonAugment.RESTORATIVE_HAIL);

            if(pokemon.moves.containsKey("Sandstorm"))
                data.registerAugment(52, PokemonAugment.RESTORATIVE_SANDSTORM);

            if(pokemon.moves.containsKey("Meteor Mash"))
                data.registerAugment(50, PokemonAugment.METEOR_SHOWER);

            //Typed Augments
            if(List.of(PokemonRarity.Rarity.EXTREME, PokemonRarity.Rarity.LEGENDARY, PokemonRarity.Rarity.MYTHICAL).contains(rarity))
            {
                data.registerAugment(24, switch(pokemon.types.get(0)) {
                    case NORMAL -> PokemonAugment.STANDARDIZATION;
                    case FIRE -> PokemonAugment.SEARING_SHOT;
                    case WATER -> PokemonAugment.DRENCH;
                    case ELECTRIC -> PokemonAugment.STATIC;
                    case GRASS -> PokemonAugment.FLORAL_HEALING;
                    case ICE -> PokemonAugment.ICY_AURA;
                    case FIGHTING -> PokemonAugment.TRUE_STRIKE;
                    case POISON -> PokemonAugment.POISONOUS_SINGE;
                    case GROUND -> PokemonAugment.GROUNDED_EMPOWERMENT;
                    case FLYING -> PokemonAugment.AERIAL_EVASION;
                    case PSYCHIC -> PokemonAugment.SURE_SHOT;
                    case BUG -> PokemonAugment.SWARM_COLLECTIVE;
                    case ROCK -> PokemonAugment.HEAVYWEIGHT_BASH;
                    case GHOST -> PokemonAugment.PHASE_SHIFTER;
                    case DRAGON -> PokemonAugment.DRACONIC_ENRAGE;
                    case DARK -> PokemonAugment.UMBRAL_ENHANCEMENTS;
                    case STEEL -> PokemonAugment.PLATED_ARMOR;
                    case FAIRY -> PokemonAugment.FLOWERING_GRACE;
                });
            }

            //Stat Augments
            Stat maxStat = Collections.max(List.of(Stat.values()), Comparator.comparingInt(pokemon.baseStats::get));

            data.registerAugment(15, switch(maxStat) {
                case HP -> PokemonAugment.HP_BOOST;
                case ATK -> PokemonAugment.ATK_BOOST;
                case DEF -> PokemonAugment.DEF_BOOST;
                case SPATK -> PokemonAugment.SPATK_BOOST;
                case SPDEF -> PokemonAugment.SPDEF_BOOST;
                case SPD -> PokemonAugment.SPD_BOOST;
            });

            //Misc

            if(List.of(PokemonRarity.Rarity.COPPER, PokemonRarity.Rarity.SILVER, PokemonRarity.Rarity.GOLD, PokemonRarity.Rarity.DIAMOND, PokemonRarity.Rarity.PLATINUM).contains(rarity))
            {
                int sumATK = pokemon.baseStats.get(Stat.ATK) + pokemon.baseStats.get(Stat.SPATK);
                int sumDEF = pokemon.baseStats.get(Stat.DEF) + pokemon.baseStats.get(Stat.SPDEF);

                if(sumATK > sumDEF) data.registerAugment(35, PokemonAugment.SUPERCHARGED);
                else data.registerAugment(35, PokemonAugment.SUPERFORTIFIED);
            }

            if(pokemon.types.size() == 1)
                data.registerAugment(40, PokemonAugment.HARMONY);

            if(pokemon.baseStats.get(Stat.SPD) >= 100)
                data.registerAugment(56, PokemonAugment.PINNACLE_EVASION);

            if(pokemon.baseStats.get(Stat.ATK) >= 90 && pokemon.baseStats.get(Stat.SPD) <= 150)
                data.registerAugment(60, PokemonAugment.PRECISION_STRIKES);

            if(pokemon.baseStats.get(Stat.ATK) >= 75)
                data.registerAugment(65, PokemonAugment.PRECISION_BURST);

            if(pokemon.baseStats.get().values().stream().mapToInt(v -> v).sum() < 600)
                data.registerAugment(65, PokemonAugment.RAW_FORCE).registerAugment(65, PokemonAugment.MODIFYING_FORCE);

            //Build
            data.build();
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

            if(this.augments.values().stream().mapToInt(EnumSet::size).sum() == MAX_AUGMENTS)
                LoggerHelper.warn(PokemonAugmentRegistry.class, "Failed to apply Augment: " + augment.getAugmentName() + " to Pokemon: " + this.pokemon + ". Maximum number of Augments reached.");
            else this.augments.get(level).add(augment);

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
