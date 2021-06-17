package com.calculusmaster.pokecord.game.moves.builder;

public class IncreasedCritDamageEffect extends DamageEffect
{
    private int crit;

    public IncreasedCritDamageEffect(int crit)
    {
        this.crit = crit;
    }

    @Override
    public String get()
    {
        this.move.critChance = crit;
        return super.get();
    }
}
