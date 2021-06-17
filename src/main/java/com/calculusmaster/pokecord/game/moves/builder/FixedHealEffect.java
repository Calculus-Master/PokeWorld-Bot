package com.calculusmaster.pokecord.game.moves.builder;

public class FixedHealEffect extends MoveEffect
{
    private int heal;

    public FixedHealEffect(int heal)
    {
        this.heal = heal;
    }

    public FixedHealEffect()
    {
        this(0);
    }

    public void set(int heal)
    {
        this.heal = heal;
    }

    public int getHeal()
    {
        return this.heal;
    }

    @Override
    public String get()
    {
        this.user.heal(this.heal);

        return this.user.getName() + " healed for **" + this.heal + "** HP!";
    }
}
