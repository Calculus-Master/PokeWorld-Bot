package com.calculusmaster.pokecord.game.moves.builder;

public class FixedMultiStrikeDamageEffect extends FixedDamageEffect
{
    private int times;

    public FixedMultiStrikeDamageEffect(int times)
    {
        this.times = times;
    }

    @Override
    public String get()
    {
        int totalDamage = 0;
        for(int i = 0; i < this.times; i++) totalDamage += this.move.getDamage(this.user, this.opponent);

        this.set(totalDamage);
        return super.get() + " " + this.move.getName() + " hit " + this.times + " times!";
    }
}
