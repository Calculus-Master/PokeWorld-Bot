package com.calculusmaster.pokecord.game.moves.builder;

import java.util.Random;

public class AccuracyChangeEffect extends MoveEffect
{
    private int stage;
    private int percent;
    private boolean userChange;

    public AccuracyChangeEffect(int stage, int percent, boolean userChange)
    {
        this.stage = stage;
        this.percent = percent;
        this.userChange = userChange;
    }

    @Override
    public String get()
    {
        if(new Random().nextInt(100) < this.percent)
        {
            (this.userChange ? this.user : this.opponent).changes().changeAccuracy(this.stage);

            return (this.userChange ? this.user : this.opponent).getName() + "'s Accuracy " + (this.stage < 0 ? " was lowered by " : " rose by ") + Math.abs(this.stage) + (Math.abs(this.stage) != 1 ? " stages" : " stage") + "!";
        }
        else return "";
    }
}
