package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;

import java.util.Random;

public class StatusConditionEffect extends MoveEffect
{
    private StatusCondition status;
    private int percent;

    public StatusConditionEffect(StatusCondition status, int percent)
    {
        this.status = status;
        this.percent = percent;
    }

    @Override
    public String get()
    {
        if(new Random().nextInt(100) < this.percent)
        {
            this.opponent.addStatusCondition(status);

            if(status.equals(StatusCondition.BOUND)) this.duel.data(opponent.getUUID()).boundTurns = 5;

            return this.opponent.getName() + " " + switch(status) {
                case BURNED -> "is burned!";
                case FROZEN -> "is frozen!";
                case PARALYZED -> "is paralyzed!";
                case ASLEEP -> "is asleep!";
                case CONFUSED -> "is confused!";
                case POISONED -> "is poisoned!";
                case FLINCHED -> "flinched!";
                case CURSED -> "is cursed!";
                case NIGHTMARE -> "has been afflicted with a Nightmare!";
                case BOUND -> "is bound!";
                case BADLY_POISONED -> "is badly poisoned!";
            };
        }
        else return "";
    }
}
