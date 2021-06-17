package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;

import java.util.Random;

public class StatusConditionEffect extends MoveEffect
{
    private StatusCondition status;
    private int percent;
    private boolean userChange;

    public StatusConditionEffect(StatusCondition status, int percent, boolean userChange)
    {
        this.status = status;
        this.percent = percent;
        this.userChange = userChange;
    }

    public StatusConditionEffect(StatusCondition status, int percent)
    {
        this(status, percent, false);
    }

    @Override
    public String get()
    {
        if(new Random().nextInt(100) < this.percent)
        {
            Pokemon p = this.userChange ? this.user : this.opponent;

            p.addStatusCondition(this.status);

            if(this.status.equals(StatusCondition.BOUND)) this.duel.data(p.getUUID()).boundTurns = 5;

            return p.getName() + " " + switch(this.status) {
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
