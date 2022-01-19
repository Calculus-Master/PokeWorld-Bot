package com.calculusmaster.pokecord.game.moves.builder;

public class FixedSelfDamageEffect extends MoveEffect
{
    private int damage;

    public FixedSelfDamageEffect(int damage)
    {
        this.damage = damage;
    }

    public FixedSelfDamageEffect()
    {
        this(0);
    }

    public void set(int damage)
    {
        this.damage = damage;
    }

    @Override
    public String get()
    {
        this.user.damage(this.damage);

        return this.user.getName() + " took " + this.damage + " damage!";
    }
}
