package com.calculusmaster.pokecord.game.moves.registry;

import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.*;
import java.util.function.Predicate;

public class MoveTutorRegistry
{
    public static final List<String> MOVE_TUTOR_MOVES = new ArrayList<>();
    public static final Map<String, Predicate<Pokemon>> VALIDATORS = new HashMap<>();

    public static void init()
    {
        register("Blast Burn", "Charizard", "Typhlosion", "Blaziken", "Infernape", "Emboar", "Delphox", "Incineroar");
        register("Hydro Cannon", "Blastoise", "Feraligatr", "Swampert", "Empoleon", "Samurott", "Greninja", "Primarina");
        register("Frenzy Plant", "Venusaur", "Meganium", "Sceptile", "Torterra", "Serperior", "Chesnaught", "Decidueye");
        register("Draco Meteor", p -> p.getType().get(0).equals(Type.DRAGON));
        register("Steel Beam", p -> p.getType().get(0).equals(Type.STEEL) && !p.getName().equals("Dusk Mane Necrozma"));
        register("Volt Tackle", p -> p.getName().contains("Pikachu"));
        register("Dragon Ascent", p -> p.getName().contains("Rayquaza"));
        register("Secret Sword", p -> p.getName().contains("Keldeo"));
        register("Relic Song", p -> p.getName().contains("Meloetta"));
    }

    private static void register(String name, String... pokemon)
    {
        register(name, p -> Arrays.asList(pokemon).contains(p.getName()));
    }

    private static void register(String name, Predicate<Pokemon> rule)
    {
        MOVE_TUTOR_MOVES.add(name);
        VALIDATORS.put(name, rule);
    }
}
