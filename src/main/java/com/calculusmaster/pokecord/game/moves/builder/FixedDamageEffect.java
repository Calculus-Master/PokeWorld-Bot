package com.calculusmaster.pokecord.game.moves.builder;

public class FixedDamageEffect extends MoveEffect
{
    private int damage;

    public FixedDamageEffect(int damage)
    {
        this.damage = damage;
    }

    @Override
    public String get()
    {
        this.opponent.damage(this.damage);

        return this.move.getDamageResult(this.opponent, this.damage);
    }
}
