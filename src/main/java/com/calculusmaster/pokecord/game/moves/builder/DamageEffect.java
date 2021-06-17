package com.calculusmaster.pokecord.game.moves.builder;

public class DamageEffect extends FixedDamageEffect
{
    @Override
    public String get()
    {
        this.set(this.move.getDamage(this.user, this.opponent));
        return super.get();
    }
}
