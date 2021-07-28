package com.calculusmaster.pokecord.game.moves.registry;

import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.moves.MoveData;
import com.calculusmaster.pokecord.util.helpers.DataHelper;

import java.util.ArrayList;
import java.util.List;

public class ZMoveRegistry
{
    public static List<String> ZMOVES = new ArrayList<>();
    public static List<String> TYPED_ZMOVES = new ArrayList<>();
    public static List<String> UNIQUE_ZMOVES = new ArrayList<>();

    public static void init()
    {
        //Generic Typed Z-Moves
        registerTyped("Savage Spin Out", Type.BUG);
        registerTyped("Black Hole Eclipse", Type.DARK);
        registerTyped("Devastating Drake", Type.DRAGON);
        registerTyped("Gigavolt Havoc", Type.ELECTRIC);
        registerTyped("Twinkle Tackle", Type.FAIRY);
        registerTyped("All Out Pummeling", Type.FIGHTING);
        registerTyped("Inferno Overdrive", Type.FIRE);
        registerTyped("Supersonic Skystrike", Type.FLYING);
        registerTyped("Never Ending Nightmare", Type.GHOST);
        registerTyped("Bloom Doom", Type.GRASS);
        registerTyped("Tectonic Rage", Type.GROUND);
        registerTyped("Subzero Slammer", Type.ICE);
        registerTyped("Breakneck Blitz", Type.NORMAL);
        registerTyped("Acid Downpour", Type.POISON);
        registerTyped("Shattered Psyche", Type.PSYCHIC);
        registerTyped("Continental Crush", Type.ROCK);
        registerTyped("Corkscrew Crash", Type.STEEL);
        registerTyped("Hydro Vortex", Type.WATER);

        //Official Unique Z-Moves
        registerUnique("Stoked Sparksurfer", Type.ELECTRIC, Category.SPECIAL, 175);
        registerUnique("Sinister Arrow Raid", Type.GHOST, Category.PHYSICAL, 180);
        registerUnique("Extreme Evoboost", Type.NORMAL, Category.STATUS, 0);
        registerUnique("Malicious Moonsault", Type.DARK, Category.PHYSICAL, 180);
        registerUnique("Clangorous Soulblaze", Type.DRAGON, Category.SPECIAL, 185);
        registerUnique("Menacing Moonraze Maelstrom", Type.GHOST, Category.SPECIAL, 200);
        registerUnique("Splintered Stormshards", Type.ROCK, Category.PHYSICAL, 190);
        registerUnique("Soul Stealing 7 Star Strike", Type.GHOST, Category.PHYSICAL, 195);
        registerUnique("Genesis Supernova", Type.PSYCHIC, Category.SPECIAL, 185);
        registerUnique("Lets Snuggle Forever", Type.FAIRY, Category.PHYSICAL, 190);
        registerUnique("Catastropika", Type.ELECTRIC, Category.PHYSICAL, 210);
        registerUnique("10,000,000 Volt Thunderbolt", Type.ELECTRIC, Category.SPECIAL, 195);
        registerUnique("Oceanic Operetta", Type.WATER, Category.SPECIAL, 195);
        registerUnique("Pulverizing Pancake", Type.NORMAL, Category.PHYSICAL, 210);
        registerUnique("Searing Sunraze Smash", Type.STEEL, Category.PHYSICAL, 200);
        registerUnique("Guardian of Alola", Type.FAIRY, Category.SPECIAL, 0);
        registerUnique("Light That Burns The Sky", Type.PSYCHIC, Category.SPECIAL, 200);

        //Custom Unique Z-Moves
        registerUnique("The Blinding One", Type.PSYCHIC, Category.SPECIAL, 220);
        registerUnique("White Hot Inferno", Type.FIRE, Category.SPECIAL, 200);
        registerUnique("Supercharged Storm Surge", Type.ELECTRIC, Category.PHYSICAL, 200);
        registerUnique("Eternal Winter", Type.ICE, Category.SPECIAL, 180);
        registerUnique("Freezing Storm Surge", Type.ICE, Category.PHYSICAL, 210);
        registerUnique("Blazing Iceferno", Type.ICE, Category.SPECIAL, 210);
        registerUnique("Tree Of Life", Type.FAIRY, Category.STATUS, 0);
        registerUnique("Cocoon Of Destruction", Type.DARK, Category.SPECIAL, 185);
        registerUnique("Dazzling Diamond Barrage", Type.ROCK, Category.PHYSICAL, 180);
        registerUnique("Decree Of Arceus", Type.NORMAL, Category.PHYSICAL, 150);
        registerUnique("Draconic Ozone Ascent", Type.DRAGON, Category.PHYSICAL, 200);
        registerUnique("Tectonic Z Wrath", Type.GROUND, Category.PHYSICAL, 180);
        registerUnique("Titanic Z Enforcer", Type.DRAGON, Category.SPECIAL, 195);
        registerUnique("Million Arrow Barrage", Type.GROUND, Category.PHYSICAL, 190);
        registerUnique("Million Wave Tsunami", Type.GROUND, Category.PHYSICAL, 190);
        registerUnique("Volcanic Steam Geyser", Type.WATER, Category.SPECIAL, 195);
        registerUnique("Primordial Tsunami", Type.WATER, Category.SPECIAL, 195);
        registerUnique("Primordial Landslide", Type.GROUND, Category.PHYSICAL, 195);
        registerUnique("Elemental Techno Overdrive", Type.NORMAL, Category.SPECIAL, 160);
        registerUnique("Quadruple Steel Smash", Type.STEEL, Category.PHYSICAL, 75);
        registerUnique("Metal Liquidation", Type.STEEL, Category.STATUS, 0);
        registerUnique("Ultra Space Hypernova", Type.DRAGON, Category.SPECIAL, 200);
        registerUnique("Timeline Shatter", Type.DRAGON, Category.SPECIAL, 200);
        registerUnique("Dark Matter Explosion", Type.DRAGON, Category.SPECIAL, 270);
        registerUnique("The Darkest Day", Type.DRAGON, Category.SPECIAL, 220);
        registerUnique("Max Particle Beam", Type.DRAGON, Category.SPECIAL, 200);
        registerUnique("Nightmare Void", Type.DARK, Category.STATUS, 0);
    }

    private static void registerTyped(String name, Type type)
    {
        MoveData data = new MoveData(name, type, null, 0, 100, new ArrayList<>());

        DataHelper.registerNewMove(name, data);
        ZMOVES.add(name);
        TYPED_ZMOVES.add(name);
    }

    private static void registerUnique(String name, Type type, Category category, int power)
    {
        MoveData data = new MoveData(name, type, category, power, 100, new ArrayList<>());

        DataHelper.registerNewMove(name, data);
        ZMOVES.add(name);
        UNIQUE_ZMOVES.add(name);
    }
}
