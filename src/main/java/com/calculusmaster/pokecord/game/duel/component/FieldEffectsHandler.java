package com.calculusmaster.pokecord.game.duel.component;

import com.calculusmaster.pokecord.game.enums.elements.FieldEffect;

import java.util.EnumSet;

public class FieldEffectsHandler
{
    private EnumSet<FieldEffect> effects;
    public int tailwindTurns;

    public FieldEffectsHandler()
    {
        this.effects = EnumSet.noneOf(FieldEffect.class);

        this.tailwindTurns = 0;
    }

    public void add(FieldEffect effect)
    {
        this.effects.add(effect);
    }

    public void add(FieldEffect effect, int turns)
    {
        this.add(effect);

        switch(effect)
        {
            case TAILWIND -> this.tailwindTurns = turns;
        }
    }

    public void remove(FieldEffect effect)
    {
        this.effects.remove(effect);
    }

    public boolean has(FieldEffect effect)
    {
        return this.effects.contains(effect);
    }
}
