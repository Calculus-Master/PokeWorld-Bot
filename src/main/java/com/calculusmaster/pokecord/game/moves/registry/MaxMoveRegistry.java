package com.calculusmaster.pokecord.game.moves.registry;

import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.moves.MoveData;

import java.util.ArrayList;
import java.util.List;

public class MaxMoveRegistry
{
    public static final List<String> MAX_MOVES = new ArrayList<>();
    public static final List<String> TYPED_MAX_MOVES = new ArrayList<>();
    public static final List<String> GMAX_MOVES = new ArrayList<>();

    public static void init()
    {
        //Generic
        registerTyped("Guard", Type.NORMAL);

        registerTyped("Flutterby", Type.BUG);
        registerTyped("Darkness", Type.DARK);
        registerTyped("Wyrmwind", Type.DRAGON);
        registerTyped("Lightning", Type.ELECTRIC);
        registerTyped("Starfall", Type.FAIRY);
        registerTyped("Knuckle", Type.FIGHTING);
        registerTyped("Flare", Type.FIRE);
        registerTyped("Airstream", Type.FLYING);
        registerTyped("Phantasm", Type.GHOST);
        registerTyped("Overgrowth", Type.GRASS);
        registerTyped("Quake", Type.ROCK);
        registerTyped("Hailstorm", Type.ICE);
        registerTyped("Strike", Type.NORMAL);
        registerTyped("Ooze", Type.POISON);
        registerTyped("Mindstorm", Type.PSYCHIC);
        registerTyped("Rockfall", Type.ROCK);
        registerTyped("Steelspike", Type.STEEL);
        registerTyped("Geyser", Type.WATER);

        //G-Max
        registerGMax("Wildfire", Type.FIRE);
        registerGMax("Befuddle", Type.BUG);
        registerGMax("Volt Crash", Type.ELECTRIC);
        registerGMax("Gold Rush", Type.NORMAL);
        registerGMax("Chi Strike", Type.FIGHTING);
        registerGMax("Terror", Type.GHOST);
        registerGMax("Foam Burst", Type.WATER);
        registerGMax("Resonance", Type.ICE);
        registerGMax("Cuddle", Type.NORMAL);
        registerGMax("Replenish", Type.NORMAL);
        registerGMax("Malodor", Type.POISON);
        registerGMax("Meltdown", Type.STEEL);
        registerGMax("Wind Rage", Type.FLYING);
        registerGMax("Gravitas", Type.PSYCHIC);
        registerGMax("Stonesurge", Type.WATER);
        registerGMax("Volcalith", Type.ROCK);
        registerGMax("Tartness", Type.GRASS);
        registerGMax("Sweetness", Type.GRASS);
        registerGMax("Sandblast", Type.GROUND);
        registerGMax("Stunshock", Type.ELECTRIC);
        registerGMax("Centiferno", Type.FIRE);
        registerGMax("Smite", Type.FAIRY);
        registerGMax("Snooze", Type.DARK);
        registerGMax("Finale", Type.FAIRY);
        registerGMax("Steelsurge", Type.STEEL);
        registerGMax("Depletion", Type.DRAGON);
        registerGMax("Vine Lash", Type.GRASS);
        registerGMax("Cannonade", Type.WATER);
        registerGMax("Drum Solo", Type.GRASS);
        registerGMax("Fireball", Type.FIRE);
        registerGMax("Hydrosnipe", Type.WATER);
        registerGMax("One Blow", Type.DARK);
        registerGMax("Rapid Flow", Type.WATER);
    }

    public static MoveData get(Type t)
    {
        return MoveData.get("Max " + switch(t) {
            case BUG -> "Flutterby";
            case DARK -> "Darkness";
            case DRAGON -> "Wyrmwind";
            case ELECTRIC -> "Lightning";
            case FAIRY -> "Starfall";
            case FIGHTING -> "Knuckle";
            case FIRE -> "Flare";
            case FLYING -> "Airstream";
            case GHOST -> "Phantasm";
            case GRASS -> "Overgrowth";
            case GROUND -> "Quake";
            case ICE -> "Hailstorm";
            case NORMAL -> "Strike";
            case POISON -> "Ooze";
            case PSYCHIC -> "Mindstorm";
            case ROCK -> "Rockfall";
            case STEEL -> "Steelspike";
            case WATER -> "Geyser";
        });
    }

    private static void registerTyped(String name, Type type)
    {
        name = "Max " + name;

        MoveData data = new MoveData(name, type, null, 0, 100, new ArrayList<>(), false, true);

        MoveData.registerNew(name, data);

        TYPED_MAX_MOVES.add(name);
        MAX_MOVES.add(name);
    }

    private static void registerGMax(String name, Type type)
    {
        name = "G Max " + name;

        MoveData data = new MoveData(name, type, null, 0, 100, new ArrayList<>(), false, true);

        MoveData.registerNew(name, data);

        GMAX_MOVES.add(name);
        MAX_MOVES.add(name);
    }
}
