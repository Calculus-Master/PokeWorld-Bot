package com.calculusmaster.pokecord.game.moves.builder;

import java.util.function.Supplier;

public class PowerBoostEffect extends MoveEffect
{
    private Supplier<Boolean> predicate;
    private double multiplier;

    public PowerBoostEffect(Supplier<Boolean> predicate, double multiplier)
    {
        this.predicate = predicate;
        this.multiplier = multiplier;
    }

    @Override
    public String get()
    {
        if(this.predicate.get())
        {
            this.move.setPower(this.multiplier);

            return this.move.getName() + "'s power was boosted!";
        }
        else return "";
    }
}
