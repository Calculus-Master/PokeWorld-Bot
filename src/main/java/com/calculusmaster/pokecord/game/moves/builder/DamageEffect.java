package com.calculusmaster.pokecord.game.moves.builder;

public class DamageEffect extends MoveEffect
{
    @Override
    public String get()
    {
        int damage = this.move.getDamage(this.user, this.opponent);

        this.opponent.damage(damage);

        return this.move.getDamageResult(this.opponent, damage);
    }
}
