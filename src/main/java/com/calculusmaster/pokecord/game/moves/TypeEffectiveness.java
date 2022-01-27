package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.enums.elements.Type;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.calculusmaster.pokecord.game.enums.elements.Type.*;

public class TypeEffectiveness
{
    private enum Effectiveness
    {
        E_NORMAL(List.of(), List.of(FIGHTING), List.of(GHOST)),
        E_FIRE(List.of(FIRE, GRASS, ICE, BUG, STEEL, FAIRY), List.of(WATER, GROUND, ROCK)),
        E_WATER(List.of(FIRE, WATER, ICE, STEEL), List.of(ELECTRIC, GRASS)),
        E_ELECTRIC(List.of(ELECTRIC, FLYING, STEEL), List.of(GROUND)),
        E_GRASS(List.of(WATER, ELECTRIC, GRASS, GROUND), List.of(FIRE, ICE, POISON, FLYING, BUG)),
        E_ICE(List.of(ICE), List.of(FIRE, FIGHTING, ROCK, STEEL)),
        E_FIGHTING(List.of(BUG, ROCK, DARK), List.of(FLYING, PSYCHIC, FAIRY), List.of(GHOST)),
        E_POISON(List.of(GRASS, FIGHTING, POISON, BUG, FAIRY), List.of(GROUND, PSYCHIC), List.of(STEEL)),
        E_GROUND(List.of(POISON, ROCK), List.of(WATER, GRASS, ICE), List.of(ELECTRIC)),
        E_FLYING(List.of(GRASS, FIGHTING, BUG), List.of(ELECTRIC, ICE, ROCK), List.of(GROUND)),
        E_PSYCHIC(List.of(FIGHTING, PSYCHIC), List.of(BUG, GHOST, DARK)),
        E_BUG(List.of(GRASS, FIGHTING, GROUND), List.of(FIRE, FLYING, ROCK)),
        E_ROCK(List.of(NORMAL, FIRE, POISON, FLYING), List.of(WATER, GRASS, FIGHTING, GROUND, STEEL)),
        E_GHOST(List.of(POISON, BUG), List.of(GHOST, DARK), List.of(NORMAL, FIGHTING)),
        E_DRAGON(List.of(FIRE, WATER, ELECTRIC, GRASS), List.of(ICE, DRAGON), List.of(FAIRY)),
        E_DARK(List.of(GHOST, DARK), List.of(FIGHTING, BUG, FAIRY), List.of(PSYCHIC)),
        E_STEEL(List.of(NORMAL, GRASS, ICE, FLYING, PSYCHIC, BUG, ROCK, DRAGON, STEEL, FAIRY), List.of(FIRE, FIGHTING, GROUND), List.of(POISON)),
        E_FAIRY(List.of(FIGHTING, BUG, DARK), List.of(POISON, STEEL), List.of(DRAGON));

        private final Map<Type, Double> values;

        Effectiveness(List<Type> notEffective, List<Type> superEffective, List<Type> zeroEffective)
        {
            this.values = new HashMap<>();

            for(Type t : notEffective) this.values.put(t, 0.5);
            for(Type t : superEffective) this.values.put(t, 2.0);
            for(Type t : zeroEffective) this.values.put(t, 0.0);

            for(Type t : Type.values()) if(!this.values.containsKey(t)) this.values.put(t, 1.0);
        }

        Effectiveness(List<Type> notEffective, List<Type> superEffective)
        {
            this(notEffective, superEffective, List.of());
        }

        static Effectiveness cast(Type t)
        {
            return Arrays.stream(values()).filter(e -> e.toString().substring("E_".length()).equals(t.toString())).findFirst().orElseThrow();
        }
    }

    public static Map<Type, Double> getEffectiveness(List<Type> types)
    {
        Map<Type, Double> total = new HashMap<>();
        for(Type t : types) Effectiveness.cast(t).values.forEach((type, effect) -> total.put(type, total.getOrDefault(type, 1.0) * effect));
        return total;
    }
}
