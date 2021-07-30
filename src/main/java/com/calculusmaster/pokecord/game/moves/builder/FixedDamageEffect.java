package com.calculusmaster.pokecord.game.moves.builder;

public class FixedDamageEffect extends MoveEffect
{
    private int damage;

    public FixedDamageEffect(int damage)
    {
        this.damage = damage;
    }

    public FixedDamageEffect()
    {
        this(0);
    }

    public void set(int damage)
    {
        this.damage = damage;
    }

    public int getDamage()
    {
        return this.damage;
    }

    @Override
    public String get()
    {
        this.opponent.damage(this.damage);

        if(this.opponent.getHealth() <= 0 && this.duel.data(this.opponent.getUUID()).endureUsed)
        {
            this.opponent.setHealth(1);
            this.duel.data(this.opponent.getUUID()).endureUsed = false;
        }

        return this.move.getDamageResult(this.opponent, this.damage);
    }
}
