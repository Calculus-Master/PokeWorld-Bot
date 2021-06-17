package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.enums.elements.Stat;

public class FractionHealEffect extends FixedHealEffect
{
    private double fraction;

    public FractionHealEffect(double fraction)
    {
        this.fraction = fraction;
    }

    @Override
    public String get()
    {
        this.set((int)(this.fraction * this.user.getStat(Stat.HP)));
        return super.get();
    }
}
