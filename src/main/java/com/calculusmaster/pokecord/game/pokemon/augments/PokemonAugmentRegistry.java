package com.calculusmaster.pokecord.game.pokemon.augments;

import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;

import java.util.*;

import static com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity.Rarity.*;

public class PokemonAugmentRegistry
{
    //Augment Slots
    public static final Map<PokemonRarity.Rarity, List<Integer>> AUGMENT_SLOTS = Map.of(
            PokemonRarity.Rarity.COPPER,        List.of(10, 15, 20, 25, 30, 35, 40, 50, 90),
            PokemonRarity.Rarity.SILVER,        List.of(10, 15, 25, 30, 40, 45, 55, 60, 90),
            PokemonRarity.Rarity.GOLD,          List.of(10, 30, 40, 50, 60, 70, 80, 90),
            PokemonRarity.Rarity.DIAMOND,       List.of(10, 25, 35, 45, 55, 75, 90),
            PokemonRarity.Rarity.PLATINUM,      List.of(10, 25, 40, 50, 80, 90),
            MYTHICAL,      List.of(10, 30, 60, 90),
            ULTRA_BEAST,   List.of(10, 35, 65, 95),
            LEGENDARY,     List.of(10, 50, 90)
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
    public static final Map<PokemonEntity, PokemonAugmentData> AUGMENT_DATA = new LinkedHashMap<>();

    public static void init()
    {
        Arrays.stream(PokemonEntity.values()).forEach(entity -> {
            PokemonAugmentData augmentData = PokemonAugmentRegistry.register(entity);
            PokemonData data = entity.data();
            PokemonRarity.Rarity rarity = entity.getRarity();

            //Unique Augments

            if(entity == PokemonEntity.NECROZMA)
            {
                augmentData.registerAugment(40, PokemonAugment.LIGHT_ABSORPTION);
                augmentData.registerAugment(55, PokemonAugment.DIFFRACTED_BEAMS);
                augmentData.registerAugment(60, PokemonAugment.PRISMATIC_CONVERGENCE);
            }
            else if(entity == PokemonEntity.NECROZMA_DUSK_MANE)
            {
                augmentData.registerAugment(40, PokemonAugment.LIGHT_ABSORPTION);
                augmentData.registerAugment(55, PokemonAugment.DIFFRACTED_BEAMS);
                augmentData.registerAugment(60, PokemonAugment.PRISMATIC_CONVERGENCE);
            }
            else if(entity == PokemonEntity.NECROZMA_DAWN_WINGS)
            {
                augmentData.registerAugment(40, PokemonAugment.PRISMATIC_MOONLIT_SHIELD);
                augmentData.registerAugment(55, PokemonAugment.DIFFRACTED_BEAMS);
                augmentData.registerAugment(60, PokemonAugment.PRISMATIC_CONVERGENCE);
            }
            else if(entity == PokemonEntity.NECROZMA_ULTRA)
            {
                augmentData.registerAugment(30, PokemonAugment.LIGHT_ABSORPTION);
                augmentData.registerAugment(65, PokemonAugment.RADIANT_DIFFRACTED_BEAMS);
                augmentData.registerAugment(70, PokemonAugment.RADIANT_PRISMATIC_CONVERGENCE);
            }
            else if(entity == PokemonEntity.MARSHADOW)
            {
                augmentData.registerAugment(25, PokemonAugment.PHANTOM_TARGETING);
                augmentData.registerAugment(35, PokemonAugment.SHADOW_PROPULSION);
                augmentData.registerAugment(50, PokemonAugment.SPECTRAL_AMPLIFICATION);
                augmentData.registerAugment(70, PokemonAugment.SPECTRAL_SUPERCHARGE);
            }
            else if(entity == PokemonEntity.REGIELEKI)
            {
                augmentData.registerAugment(50, PokemonAugment.ELECTRIFIED_HYPER_SPEED);
            }
            else if(entity == PokemonEntity.VICTINI)
            {
                augmentData.registerAugment(30, PokemonAugment.FINAL_RESORT_V);
                augmentData.registerAugment(40, PokemonAugment.V_RUSH);
                augmentData.registerAugment(65, PokemonAugment.VICTORY_RESOLVE);
                augmentData.registerAugment(80, PokemonAugment.SHINING_STAR);
                augmentData.registerAugment(90, PokemonAugment.VICTORY_ENSURED);
            }

            //Stat Augments
            Stat maxStat = Collections.max(List.of(Stat.values()), Comparator.comparingInt(data.getBaseStats()::get));

            augmentData.registerAugment(15, switch(maxStat) {
                case HP -> PokemonAugment.HP_BOOST;
                case ATK -> PokemonAugment.ATK_BOOST;
                case DEF -> PokemonAugment.DEF_BOOST;
                case SPATK -> PokemonAugment.SPATK_BOOST;
                case SPDEF -> PokemonAugment.SPDEF_BOOST;
                case SPD -> PokemonAugment.SPD_BOOST;
            });

            //Move Augments

            if(data.getLevelUpMoves().keySet().stream().anyMatch(Move.PUNCH_MOVES::contains))
                augmentData.registerAugment(25, PokemonAugment.WEIGHTED_PUNCH);

            if(!List.of(MYTHICAL, ULTRA_BEAST, LEGENDARY).contains(rarity))
                augmentData.registerAugment(100, PokemonAugment.Z_AFFINITY);

            if(data.getLevelUpMoves().containsKey(MoveEntity.HAIL) || data.getLevelUpMoves().containsKey(MoveEntity.SNOWSCAPE))
                augmentData.registerAugment(52, PokemonAugment.RESTORATIVE_HAIL);

            if(data.getLevelUpMoves().containsKey(MoveEntity.SANDSTORM))
                augmentData.registerAugment(52, PokemonAugment.RESTORATIVE_SANDSTORM);

            if(data.getLevelUpMoves().containsKey(MoveEntity.METEOR_MASH))
                augmentData.registerAugment(50, PokemonAugment.METEOR_SHOWER);

            //Typed Augments
            if(!EnumSet.of(LEGENDARY, ULTRA_BEAST, MYTHICAL).contains(rarity))
            {
                augmentData.registerAugment(24, switch(data.getTypes().get(0)) {
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

            //Misc

            if(List.of(COPPER, SILVER, GOLD, DIAMOND, PLATINUM).contains(rarity))
            {
                int sumATK = data.getBaseStats().get(Stat.ATK) + data.getBaseStats().get(Stat.SPATK);
                int sumDEF = data.getBaseStats().get(Stat.DEF) + data.getBaseStats().get(Stat.SPDEF);

                if(sumATK > sumDEF) augmentData.registerAugment(35, PokemonAugment.SUPERCHARGED);
                else augmentData.registerAugment(35, PokemonAugment.SUPERFORTIFIED);
            }

            if(data.getTypes().size() == 1)
                augmentData.registerAugment(40, PokemonAugment.HARMONY);

            if(data.getBaseStats().get(Stat.SPD) >= 100)
                augmentData.registerAugment(56, PokemonAugment.PINNACLE_EVASION);

            if(data.getBaseStats().get(Stat.ATK) >= 90 && data.getBaseStats().get(Stat.SPD) <= 150)
                augmentData.registerAugment(60, PokemonAugment.PRECISION_STRIKES);

            if(data.getBaseStats().get(Stat.ATK) >= 75)
                augmentData.registerAugment(65, PokemonAugment.PRECISION_BURST);

            if(data.getBaseStats().get().values().stream().mapToInt(v -> v).sum() < 600)
                augmentData.registerAugment(65, PokemonAugment.RAW_FORCE).registerAugment(65, PokemonAugment.MODIFYING_FORCE);

            //Build
            augmentData.build();
        });
    }

    private static PokemonAugmentData register(PokemonEntity pokemonEntity)
    {
        PokemonAugmentData data = new PokemonAugmentData();
        data.pokemon = pokemonEntity;
        data.augments = new HashMap<>();
        return data;
    }

    public static class PokemonAugmentData
    {
        private PokemonEntity pokemon;
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
