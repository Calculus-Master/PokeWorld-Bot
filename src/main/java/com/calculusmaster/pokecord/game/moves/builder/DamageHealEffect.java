package com.calculusmaster.pokecord.game.moves.builder;

public class DamageHealEffect extends FixedHealEffect
{
    private double fraction;
    private int damageDealt;

    public DamageHealEffect(double fraction)
    {
        this.fraction = fraction;
    }

    public void set(int damageDealt)
    {
        this.damageDealt = damageDealt;
    }

    @Override
    public String get()
    {
        super.set((int)(this.fraction * this.damageDealt));

        return super.get();
    }
}
