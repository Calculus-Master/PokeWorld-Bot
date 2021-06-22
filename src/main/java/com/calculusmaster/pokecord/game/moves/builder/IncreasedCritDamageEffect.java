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
        this.move.critChance = Math.max(this.move.critChance, this.crit);
        return super.get();
    }
}
