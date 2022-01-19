package com.calculusmaster.pokecord.game.moves.builder;

public class FractionSelfDamageEffect extends FixedSelfDamageEffect
{
    private double fraction;

    public FractionSelfDamageEffect(double fraction)
    {
        this.fraction = fraction;
    }

    @Override
    public String get()
    {
        this.set((int)(this.fraction * this.user.getMaxHealth()));

        return super.get();
    }
}
