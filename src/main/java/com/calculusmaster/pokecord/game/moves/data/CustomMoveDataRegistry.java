package com.calculusmaster.pokecord.game.moves.data;

import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Type;

import java.util.HashMap;
import java.util.Map;

import static com.calculusmaster.pokecord.game.moves.data.MoveEntity.*;

public class CustomMoveDataRegistry
{
    private static final Map<Type, MoveEntity> TYPED_Z_MOVES = new HashMap<>();
    private static final Map<Type, MoveEntity> TYPED_MAX_MOVES = new HashMap<>();

    public static void init()
    {
        //Z-Moves (Typed)
        final ZMoveRegistryConsumer RegisterTypedZMove = (e, n, t) -> {
            MoveEntity.MOVE_ENTITY_DATA.put(e, new MoveData(e, n, t, null, 0, 100));
            TYPED_Z_MOVES.put(t, e);
        };

        RegisterTypedZMove.register(SAVAGE_SPIN_OUT, "Savage Spin Out", Type.BUG);
        RegisterTypedZMove.register(BLACK_HOLE_ECLIPSE, "Black Hole Eclipse", Type.DARK);
        RegisterTypedZMove.register(DEVASTATING_DRAKE, "Devastating Drake", Type.DRAGON);
        RegisterTypedZMove.register(GIGAVOLT_HAVOC, "Gigavolt Havoc", Type.ELECTRIC);
        RegisterTypedZMove.register(TWINKLE_TACKLE, "Twinkle Tackle", Type.FAIRY);
        RegisterTypedZMove.register(ALL_OUT_PUMMELING, "All-Out Pummeling", Type.FIGHTING);
        RegisterTypedZMove.register(INFERNO_OVERDRIVE, "Inferno Overdrive", Type.FIRE);
        RegisterTypedZMove.register(SUPERSONIC_SKYSTRIKE, "Supersonic Skystrike", Type.FLYING);
        RegisterTypedZMove.register(NEVER_ENDING_NIGHTMARE, "Never Ending Nightmare", Type.GHOST);
        RegisterTypedZMove.register(BLOOM_DOOM, "Bloom Doom", Type.GRASS);
        RegisterTypedZMove.register(TECTONIC_RAGE, "Tectonic Rage", Type.GROUND);
        RegisterTypedZMove.register(SUBZERO_SLAMMER, "Subzero Slammer", Type.ICE);
        RegisterTypedZMove.register(BREAKNECK_BLITZ, "Breakneck Blitz", Type.NORMAL);
        RegisterTypedZMove.register(ACID_DOWNPOUR, "Acid Downpour", Type.POISON);
        RegisterTypedZMove.register(SHATTERED_PSYCHE, "Shattered Psyche", Type.PSYCHIC);
        RegisterTypedZMove.register(CONTINENTAL_CRUSH, "Continental Crush", Type.ROCK);
        RegisterTypedZMove.register(CORKSCREW_CRASH, "Corkscrew Crash", Type.STEEL);
        RegisterTypedZMove.register(HYDRO_VORTEX, "Hydro Vortex", Type.WATER);


        //Z-Moves (Unique)
        final UniqueZMoveRegistryConsumer RegisterUniqueZMove = (e, n, t, c, p) -> MoveEntity.MOVE_ENTITY_DATA.put(e, new MoveData(e, n, t, c, p, 100));

        RegisterUniqueZMove.register(STOKED_SPARKSURFER, "Stoked Sparksurfer", Type.ELECTRIC, Category.SPECIAL, 175);
        RegisterUniqueZMove.register(SINISTER_ARROW_RAID, "Sinister Arrow Raid", Type.GHOST, Category.PHYSICAL, 180);
        RegisterUniqueZMove.register(EXTREME_EVOBOOST, "Extreme Evoboost", Type.NORMAL, Category.STATUS, 0);
        RegisterUniqueZMove.register(MALICIOUS_MOONSAULT, "Malicious Moonsault", Type.DARK, Category.PHYSICAL, 180);
        RegisterUniqueZMove.register(CLANGOROUS_SOULBLAZE, "Clangorous Soulblaze", Type.DRAGON, Category.SPECIAL, 185);
        RegisterUniqueZMove.register(MENACING_MOONRAZE_MAELSTROM, "Menacing Moonraze Maelstrom", Type.GHOST, Category.SPECIAL, 200);
        RegisterUniqueZMove.register(SPLINTERED_STORMSHARDS, "Splintered Stormshards", Type.ROCK, Category.PHYSICAL, 190);
        RegisterUniqueZMove.register(SOUL_STEALING_7_STAR_STRIKE, "Soul-Stealing 7-Star Strike", Type.GHOST, Category.PHYSICAL, 195);
        RegisterUniqueZMove.register(GENESIS_SUPERNOVA, "Genesis Supernova", Type.PSYCHIC, Category.SPECIAL, 185);
        RegisterUniqueZMove.register(LETS_SNUGGLE_FOREVER, "Lets Snuggle Forever", Type.FAIRY, Category.PHYSICAL, 190);
        RegisterUniqueZMove.register(CATASTROPIKA, "Catastropika", Type.ELECTRIC, Category.PHYSICAL, 210);
        RegisterUniqueZMove.register(TEN_MILLION_VOLT_THUNDERBOLT, "10,000,000 Volt Thunderbolt", Type.ELECTRIC, Category.SPECIAL, 195);
        RegisterUniqueZMove.register(OCEANIC_OPERETTA, "Oceanic Operetta", Type.WATER, Category.SPECIAL, 195);
        RegisterUniqueZMove.register(PULVERIZING_PANCAKE, "Pulverizing Pancake", Type.NORMAL, Category.PHYSICAL, 210);
        RegisterUniqueZMove.register(SEARING_SUNRAZE_SMASH, "Searing Sunraze Smash", Type.STEEL, Category.PHYSICAL, 200);
        RegisterUniqueZMove.register(GUARDIAN_OF_ALOLA, "Guardian of Alola", Type.FAIRY, Category.SPECIAL, 0);
        RegisterUniqueZMove.register(LIGHT_THAT_BURNS_THE_SKY, "Light That Burns The Sky", Type.PSYCHIC, Category.SPECIAL, 200);

        //Z-Moves (Custom)
        final UniqueZMoveRegistryConsumer RegisterCustomUniqueZMove = (e, n, t, c, p) -> MoveEntity.MOVE_ENTITY_DATA.put(e, new MoveData(e, n, t, c, p, 100));

        RegisterCustomUniqueZMove.register(THE_BLINDING_ONE, "The Blinding One", Type.PSYCHIC, Category.SPECIAL, 220);
        RegisterCustomUniqueZMove.register(WHITE_HOT_INFERNO, "White Hot Inferno", Type.FIRE, Category.SPECIAL, 200);
        RegisterCustomUniqueZMove.register(SUPERCHARGED_STORM_SURGE, "Supercharged Storm Surge", Type.ELECTRIC, Category.PHYSICAL, 200);
        RegisterCustomUniqueZMove.register(ETERNAL_WINTER, "Eternal Winter", Type.ICE, Category.SPECIAL, 180);
        RegisterCustomUniqueZMove.register(FREEZING_STORM_SURGE, "Freezing Storm Surge", Type.ICE, Category.PHYSICAL, 210);
        RegisterCustomUniqueZMove.register(BLAZING_ICEFERNO, "Blazing Iceferno", Type.ICE, Category.SPECIAL, 210);
        RegisterCustomUniqueZMove.register(TREE_OF_LIFE, "Tree Of Life", Type.FAIRY, Category.STATUS, 0);
        RegisterCustomUniqueZMove.register(COCOON_OF_DESTRUCTION, "Cocoon Of Destruction", Type.DARK, Category.SPECIAL, 185);
        RegisterCustomUniqueZMove.register(DAZZLING_DIAMOND_BARRAGE, "Dazzling Diamond Barrage", Type.ROCK, Category.PHYSICAL, 180);
        RegisterCustomUniqueZMove.register(DECREE_OF_ARCEUS, "Decree Of Arceus", Type.NORMAL, Category.PHYSICAL, 150);
        RegisterCustomUniqueZMove.register(DRACONIC_OZONE_ASCENT, "Draconic Ozone Ascent", Type.DRAGON, Category.PHYSICAL, 200);
        RegisterCustomUniqueZMove.register(TECTONIC_Z_WRATH, "Tectonic Z Wrath", Type.GROUND, Category.PHYSICAL, 180);
        RegisterCustomUniqueZMove.register(TITANIC_Z_ENFORCER, "Titanic Z Enforcer", Type.DRAGON, Category.SPECIAL, 195);
        RegisterCustomUniqueZMove.register(MILLION_ARROW_BARRAGE, "Million Arrow Barrage", Type.GROUND, Category.PHYSICAL, 190);
        RegisterCustomUniqueZMove.register(MILLION_WAVE_TSUNAMI, "Million Wave Tsunami", Type.GROUND, Category.PHYSICAL, 190);
        RegisterCustomUniqueZMove.register(VOLCANIC_STEAM_GEYSER, "Volcanic Steam Geyser", Type.WATER, Category.SPECIAL, 195);
        RegisterCustomUniqueZMove.register(PRIMORDIAL_TSUNAMI, "Primordial Tsunami", Type.WATER, Category.SPECIAL, 195);
        RegisterCustomUniqueZMove.register(PRIMORDIAL_LANDSLIDE, "Primordial Landslide", Type.GROUND, Category.PHYSICAL, 195);
        RegisterCustomUniqueZMove.register(ELEMENTAL_TECHNO_OVERDRIVE, "Elemental Techno Overdrive", Type.NORMAL, Category.SPECIAL, 160);
        RegisterCustomUniqueZMove.register(QUADRUPLE_STEEL_SMASH, "Quadruple Steel Smash", Type.STEEL, Category.PHYSICAL, 75);
        RegisterCustomUniqueZMove.register(METAL_LIQUIDATION, "Metal Liquidation", Type.STEEL, Category.STATUS, 0);
        RegisterCustomUniqueZMove.register(ULTRA_SPACE_HYPERNOVA, "Ultra Space Hypernova", Type.DRAGON, Category.SPECIAL, 200);
        RegisterCustomUniqueZMove.register(TIMELINE_SHATTER, "Timeline Shatter", Type.DRAGON, Category.SPECIAL, 200);
        RegisterCustomUniqueZMove.register(DARK_MATTER_EXPLOSION, "Dark Matter Explosion", Type.DRAGON, Category.SPECIAL, 270);
        RegisterCustomUniqueZMove.register(THE_DARKEST_DAY, "The Darkest Day", Type.DRAGON, Category.SPECIAL, 220);
        RegisterCustomUniqueZMove.register(MAX_PARTICLE_BEAM, "Max Particle Beam", Type.DRAGON, Category.SPECIAL, 200);
        RegisterCustomUniqueZMove.register(NIGHTMARE_VOID, "Nightmare Void", Type.DARK, Category.STATUS, 0);

        //Max Guard
        MOVE_ENTITY_DATA.put(
                MAX_GUARD,
                new MoveData(MAX_GUARD, "Max Guard", Type.NORMAL, Category.STATUS, 0, 100)
        );

        //Max Moves
        final MaxMoveRegistryConsumer RegisterMaxMove = (e, n, t) -> {
            MoveEntity.MOVE_ENTITY_DATA.put(e, new MoveData(e, "Max " + n, t, null, 0, 100));
            TYPED_MAX_MOVES.put(t, e);
        };

        RegisterMaxMove.register(MAX_FLUTTERBY, "Flutterby", Type.BUG);
        RegisterMaxMove.register(MAX_DARKNESS, "Darkness", Type.DARK);
        RegisterMaxMove.register(MAX_WYRMWIND, "Wyrmwind", Type.DRAGON);
        RegisterMaxMove.register(MAX_LIGHTNING, "Lightning", Type.ELECTRIC);
        RegisterMaxMove.register(MAX_STARFALL, "Starfall", Type.FAIRY);
        RegisterMaxMove.register(MAX_KNUCKLE, "Knuckle", Type.FIGHTING);
        RegisterMaxMove.register(MAX_FLARE, "Flare", Type.FIRE);
        RegisterMaxMove.register(MAX_AIRSTREAM, "Airstream", Type.FLYING);
        RegisterMaxMove.register(MAX_PHANTASM, "Phantasm", Type.GHOST);
        RegisterMaxMove.register(MAX_OVERGROWTH, "Overgrowth", Type.GRASS);
        RegisterMaxMove.register(MAX_QUAKE, "Quake", Type.ROCK);
        RegisterMaxMove.register(MAX_HAILSTORM, "Hailstorm", Type.ICE);
        RegisterMaxMove.register(MAX_STRIKE, "Strike", Type.NORMAL);
        RegisterMaxMove.register(MAX_OOZE, "Ooze", Type.POISON);
        RegisterMaxMove.register(MAX_MINDSTORM, "Mindstorm", Type.PSYCHIC);
        RegisterMaxMove.register(MAX_ROCKFALL, "Rockfall", Type.ROCK);
        RegisterMaxMove.register(MAX_STEELSPIKE, "Steelspike", Type.STEEL);
        RegisterMaxMove.register(MAX_GEYSER, "Geyser", Type.WATER);
        
        //G-Max Moves
        final MaxMoveRegistryConsumer RegisterGMaxMove = (e, n, t) -> MoveEntity.MOVE_ENTITY_DATA.put(e, new MoveData(e, "G-Max " + n, t, null, 0, 100));
        
        RegisterGMaxMove.register(GMAX_WILDFIRE, "Wildfire", Type.FIRE);
        RegisterGMaxMove.register(GMAX_BEFUDDLE, "Befuddle", Type.BUG);
        RegisterGMaxMove.register(GMAX_VOLT_CRASH, "Volt Crash", Type.ELECTRIC);
        RegisterGMaxMove.register(GMAX_GOLD_RUSH, "Gold Rush", Type.NORMAL);
        RegisterGMaxMove.register(GMAX_CHI_STRIKE, "Chi Strike", Type.FIGHTING);
        RegisterGMaxMove.register(GMAX_TERROR, "Terror", Type.GHOST);
        RegisterGMaxMove.register(GMAX_FOAM_BURST, "Foam Burst", Type.WATER);
        RegisterGMaxMove.register(GMAX_RESONANCE, "Resonance", Type.ICE);
        RegisterGMaxMove.register(GMAX_CUDDLE, "Cuddle", Type.NORMAL);
        RegisterGMaxMove.register(GMAX_REPLENISH, "Replenish", Type.NORMAL);
        RegisterGMaxMove.register(GMAX_MALODOR, "Malodor", Type.POISON);
        RegisterGMaxMove.register(GMAX_MELTDOWN, "Meltdown", Type.STEEL);
        RegisterGMaxMove.register(GMAX_WIND_RAGE, "Wind Rage", Type.FLYING);
        RegisterGMaxMove.register(GMAX_GRAVITAS, "Gravitas", Type.PSYCHIC);
        RegisterGMaxMove.register(GMAX_STONESURGE, "Stonesurge", Type.WATER);
        RegisterGMaxMove.register(GMAX_VOLCALITH, "Volcalith", Type.ROCK);
        RegisterGMaxMove.register(GMAX_TARTNESS, "Tartness", Type.GRASS);
        RegisterGMaxMove.register(GMAX_SWEETNESS, "Sweetness", Type.GRASS);
        RegisterGMaxMove.register(GMAX_SANDBLAST, "Sandblast", Type.GROUND);
        RegisterGMaxMove.register(GMAX_STUNSHOCK, "Stunshock", Type.ELECTRIC);
        RegisterGMaxMove.register(GMAX_CENTIFERNO, "Centiferno", Type.FIRE);
        RegisterGMaxMove.register(GMAX_SMITE, "Smite", Type.FAIRY);
        RegisterGMaxMove.register(GMAX_SNOOZE, "Snooze", Type.DARK);
        RegisterGMaxMove.register(GMAX_FINALE, "Finale", Type.FAIRY);
        RegisterGMaxMove.register(GMAX_STEELSURGE, "Steelsurge", Type.STEEL);
        RegisterGMaxMove.register(GMAX_DEPLETION, "Depletion", Type.DRAGON);
        RegisterGMaxMove.register(GMAX_VINE_LASH, "Vine Lash", Type.GRASS);
        RegisterGMaxMove.register(GMAX_CANNONADE, "Cannonade", Type.WATER);
        RegisterGMaxMove.register(GMAX_DRUM_SOLO, "Drum Solo", Type.GRASS);
        RegisterGMaxMove.register(GMAX_FIREBALL, "Fireball", Type.FIRE);
        RegisterGMaxMove.register(GMAX_HYDROSNIPE, "Hydrosnipe", Type.WATER);
        RegisterGMaxMove.register(GMAX_ONE_BLOW, "One Blow", Type.DARK);
        RegisterGMaxMove.register(GMAX_RAPID_FLOW, "Rapid Flow", Type.WATER);
    }

    public static MoveEntity getZMove(Type t)
    {
        return TYPED_Z_MOVES.get(t);
    }

    public static MoveEntity getMaxMove(Type t)
    {
        return TYPED_MAX_MOVES.get(t);
    }

    public static boolean isTypedZMove(MoveEntity move)
    {
        return TYPED_Z_MOVES.containsValue(move);
    }

    private interface ZMoveRegistryConsumer
    {
        void register(MoveEntity entity, String name, Type type);
    }

    private interface UniqueZMoveRegistryConsumer
    {
        void register(MoveEntity entity, String name, Type type, Category category, int basePower);
    }
    
    private interface MaxMoveRegistryConsumer
    {
        void register(MoveEntity entity, String name, Type type);
    }
}
