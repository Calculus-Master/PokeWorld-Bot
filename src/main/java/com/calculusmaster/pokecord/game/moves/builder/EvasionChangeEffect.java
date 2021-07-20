package com.calculusmaster.pokecord.game.moves.builder;

import java.util.Random;

public class EvasionChangeEffect extends MoveEffect
{
    private int stage;
    private int percent;
    private boolean userChange;

    public EvasionChangeEffect(int stage, int percent, boolean userChange)
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
            (this.userChange ? this.user : this.opponent).changeEvasionStage(this.stage);

            return (this.userChange ? this.user : this.opponent).getName() + "'s Evasion " + (this.stage < 0 ? " was lowered by " : " rose by ") + this.stage + (Math.abs(this.stage) != 1 ? " stages" : "stage") + "!";
        }
        else return "";
    }
}
