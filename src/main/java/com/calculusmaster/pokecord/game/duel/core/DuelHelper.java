package com.calculusmaster.pokecord.game.duel.core;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.component.DuelActionType;
import com.calculusmaster.pokecord.game.duel.players.Player;
import com.calculusmaster.pokecord.game.duel.players.TrainerPlayer;
import com.calculusmaster.pokecord.game.duel.players.UserPlayer;
import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.data.CustomMoveDataRegistry;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.evolution.GigantamaxRegistry;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DuelHelper
{
    public static final Map<String, Duel> DUELS = new HashMap<>(20);
    public static final String BACKGROUND = "https://cutewallpaper.org/21/pokemon-battle-background/Battle-backgrounds-for-Pokemon-Showdown-Smogon-Forums.jpg";

    public static boolean isInDuel(String id)
    {
        return DuelHelper.instance(id) != null;
    }

    public static Duel instance(String id)
    {
        return DUELS.getOrDefault(id, null);
    }

    public static void delete(String id)
    {
        Duel d = DuelHelper.instance(id);
        if(d == null) return;

        for(Player p : d.getPlayers()) DUELS.remove(p.ID);
    }

    public static Duel findDuel(Pokemon pokemon)
    {
        return DuelHelper.DUELS.values().stream().filter(duel -> Arrays.stream(duel.getPlayers()).anyMatch(p -> p.team.contains(pokemon))).findFirst().orElse(null);
    }

    //Core Components

    public record TurnAction(DuelActionType action, int moveInd, int swapInd) {}

    //Z-Moves and Max Moves

    public static Move getZMove(Player p, Move baseMove)
    {
        if(!(p instanceof UserPlayer) && !(p instanceof TrainerPlayer))
        {
            LoggerHelper.error(DuelHelper.class, "Invalid Player Type attempting to use a Z-Crystal! " + p.getClass().getName());
            return baseMove;
        }

        ZCrystal z = p instanceof UserPlayer up ? up.data.getInventory().getEquippedZCrystal() : ((TrainerPlayer)(p)).getData().getZCrystal();
        MoveEntity fallback = MoveEntity.TACKLE;

        if(z == null) return new Move(fallback);

        MoveEntity entityZMove = switch(z)
        {
            //Types
            case BUGINIUM_Z -> MoveEntity.SAVAGE_SPIN_OUT;
            case DARKINIUM_Z -> MoveEntity.BLACK_HOLE_ECLIPSE;
            case DRAGONIUM_Z -> MoveEntity.DEVASTATING_DRAKE;
            case ELECTRIUM_Z -> MoveEntity.GIGAVOLT_HAVOC;
            case FAIRIUM_Z -> MoveEntity.TWINKLE_TACKLE;
            case FIGHTINIUM_Z -> MoveEntity.ALL_OUT_PUMMELING;
            case FIRIUM_Z -> MoveEntity.INFERNO_OVERDRIVE;
            case FLYINIUM_Z -> MoveEntity.SUPERSONIC_SKYSTRIKE;
            case GHOSTIUM_Z -> MoveEntity.NEVER_ENDING_NIGHTMARE;
            case GRASSIUM_Z -> MoveEntity.BLOOM_DOOM;
            case GROUNDIUM_Z -> MoveEntity.TECTONIC_RAGE;
            case ICIUM_Z -> MoveEntity.SUBZERO_SLAMMER;
            case NORMALIUM_Z -> MoveEntity.BREAKNECK_BLITZ;
            case POISONIUM_Z -> MoveEntity.ACID_DOWNPOUR;
            case PSYCHIUM_Z -> MoveEntity.SHATTERED_PSYCHE;
            case ROCKIUM_Z -> MoveEntity.CONTINENTAL_CRUSH;
            case STEELIUM_Z -> MoveEntity.CORKSCREW_CRASH;
            case WATERIUM_Z -> MoveEntity.HYDRO_VORTEX;
            //Uniques
            case ALORAICHIUM_Z -> MoveEntity.STOKED_SPARKSURFER;
            case DECIDIUM_Z -> MoveEntity.SINISTER_ARROW_RAID;
            case EEVIUM_Z -> MoveEntity.EXTREME_EVOBOOST;
            case INCINIUM_Z -> MoveEntity.MALICIOUS_MOONSAULT;
            case KOMMOIUM_Z -> MoveEntity.CLANGOROUS_SOULBLAZE;
            case LUNALIUM_Z -> MoveEntity.MENACING_MOONRAZE_MAELSTROM;
            case LYCANIUM_Z -> MoveEntity.SPLINTERED_STORMSHARDS;
            case MARSHADIUM_Z -> MoveEntity.SOUL_STEALING_7_STAR_STRIKE;
            case MEWNIUM_Z -> MoveEntity.GENESIS_SUPERNOVA;
            case MIMIKIUM_Z -> MoveEntity.LETS_SNUGGLE_FOREVER;
            case PIKANIUM_Z -> MoveEntity.CATASTROPIKA;
            case PIKASHUNIUM_Z -> MoveEntity.TEN_MILLION_VOLT_THUNDERBOLT;
            case PRIMARIUM_Z -> MoveEntity.OCEANIC_OPERETTA;
            case SNORLIUM_Z -> MoveEntity.PULVERIZING_PANCAKE;
            case SOLGANIUM_Z -> MoveEntity.SEARING_SUNRAZE_SMASH;
            case TAPUNIUM_Z -> MoveEntity.GUARDIAN_OF_ALOLA;
            case ULTRANECROZIUM_Z -> switch(baseMove.getEntity()) {
                case PHOTON_GEYSER -> MoveEntity.LIGHT_THAT_BURNS_THE_SKY;
                case PRISMATIC_LASER -> MoveEntity.THE_BLINDING_ONE;
                default -> fallback;
            };
            //Custom Uniques
            case RESHIRIUM_Z -> MoveEntity.WHITE_HOT_INFERNO;
            case ZEKRIUM_Z -> MoveEntity.SUPERCHARGED_STORM_SURGE;
            case KYURIUM_Z -> switch (baseMove.getEntity()) {
                case GLACIATE -> MoveEntity.ETERNAL_WINTER;
                case FREEZE_SHOCK -> MoveEntity.FREEZING_STORM_SURGE;
                case ICE_BURN -> MoveEntity.BLAZING_ICEFERNO;
                default -> fallback;
            };
            case XERNIUM_Z -> MoveEntity.TREE_OF_LIFE;
            case YVELTIUM_Z -> MoveEntity.COCOON_OF_DESTRUCTION;
            case DIANCIUM_Z -> MoveEntity.DAZZLING_DIAMOND_BARRAGE;
            case ARCEIUM_Z -> MoveEntity.DECREE_OF_ARCEUS;
            case RAYQUAZIUM_Z -> MoveEntity.DRACONIC_OZONE_ASCENT;
            case ZYGARDIUM_Z -> switch (baseMove.getEntity()) {
                case LANDS_WRATH -> MoveEntity.TECTONIC_Z_WRATH;
                case CORE_ENFORCER -> MoveEntity.TITANIC_Z_ENFORCER;
                case THOUSAND_ARROWS -> MoveEntity.MILLION_ARROW_BARRAGE;
                case THOUSAND_WAVES -> MoveEntity.MILLION_WAVE_TSUNAMI;
                default -> fallback;
            };
            case VOLCANIUM_Z -> MoveEntity.VOLCANIC_STEAM_GEYSER;
            case KYOGRIUM_Z -> MoveEntity.PRIMORDIAL_TSUNAMI;
            case GROUDONIUM_Z -> MoveEntity.PRIMORDIAL_LANDSLIDE;
            case GENESECTIUM_Z -> MoveEntity.ELEMENTAL_TECHNO_OVERDRIVE;
            case MELMETALIUM_Z -> switch(baseMove.getEntity()) {
                case DOUBLE_IRON_BASH -> MoveEntity.QUADRUPLE_STEEL_SMASH;
                case ACID_ARMOR -> MoveEntity.METAL_LIQUIDATION;
                default -> fallback;
            };
            case DIALGIUM_Z -> MoveEntity.TIMELINE_SHATTER;
            case PALKIUM_Z -> MoveEntity.ULTRA_SPACE_HYPERNOVA;
            case GIRATINIUM_Z -> MoveEntity.DARK_MATTER_EXPLOSION;
            case ETERNIUM_Z -> switch(baseMove.getEntity()) {
                case ETERNABEAM -> MoveEntity.THE_DARKEST_DAY;
                case DYNAMAX_CANNON -> MoveEntity.MAX_PARTICLE_BEAM;
                default -> fallback;
            };
            case DARKRAIUM_Z -> MoveEntity.NIGHTMARE_VOID;
        };

        Move ZMove = new Move(entityZMove);

        if(CustomMoveDataRegistry.isTypedZMove(entityZMove))
        {
            int ZPower;

            if(baseMove.getPower() <= 55) ZPower = 100;
            else if(baseMove.getPower() <= 65) ZPower = 120;
            else if(baseMove.getPower() <= 75) ZPower = 140;
            else if(baseMove.getPower() <= 85) ZPower = 160;
            else if(baseMove.getPower() <= 95) ZPower = 175;
            else if(baseMove.getPower() <= 100) ZPower = 180;
            else if(baseMove.getPower() <= 110) ZPower = 185;
            else if(baseMove.getPower() <= 125) ZPower = 190;
            else if(baseMove.getPower() <= 130) ZPower = 195;
            else ZPower = 200;

            ZPower = switch(baseMove.getEntity()) {
                case MEGA_DRAIN -> 120;
                case CORE_ENFORCER -> 140;
                case WEATHER_BALL, HEX -> 160;
                case FLYING_PRESS -> 170;
                case GEAR_GRIND, FISSURE, GUILLOTINE, HORN_DRILL, SHEER_COLD -> 180;
                case V_CREATE -> 220;
                default -> ZPower;
            };

            ZMove.setPower(ZPower);
            ZMove.setCategory(baseMove.getCategory());
        }

        return ZMove;
    }

    public static Move getMaxMove(Pokemon p, Move baseMove)
    {
        Move maxMove;

        if(baseMove.is(Category.STATUS)) maxMove = new Move(MoveEntity.MAX_GUARD);
        else if(GigantamaxRegistry.hasGMax(p.getEntity()) && GigantamaxRegistry.getGMaxMove(p.getEntity()).data().getType().equals(baseMove.getType()))
            maxMove = new Move(GigantamaxRegistry.getGMaxMove(p.getEntity()));
        else maxMove = new Move(CustomMoveDataRegistry.getMaxMove(baseMove.getType()));

        int maxPower;

        boolean isDecreased = maxMove.is(MoveEntity.MAX_KNUCKLE, MoveEntity.MAX_OOZE);

        if(baseMove.getCategory().equals(Category.STATUS)) maxPower = 0;
        else if(baseMove.getPower() <= 40) maxPower = isDecreased ? 70 : 90;
        else if(baseMove.getPower() <= 50) maxPower = isDecreased ? 75 : 100;
        else if(baseMove.getPower() <= 60) maxPower = isDecreased ? 80 : 110;
        else if(baseMove.getPower() <= 70) maxPower = isDecreased ? 85 : 120;
        else if(baseMove.getPower() <= 100) maxPower = isDecreased ? 90 : 130;
        else if(baseMove.getPower() <= 140) maxPower = isDecreased ? 95 : 140;
        else maxPower = isDecreased ? 100 : 150;

        maxMove.setPower(maxPower);
        maxMove.setCategory(baseMove.getCategory());

        return maxMove;
    }
}
