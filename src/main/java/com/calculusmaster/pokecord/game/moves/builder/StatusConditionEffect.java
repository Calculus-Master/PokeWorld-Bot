package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

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

    @Override
    public String get()
    {
        if(new Random().nextInt(100) < this.percent)
        {
            Pokemon p = this.userChange ? this.user : this.opponent;

            if(p.hasStatusCondition(this.status)) return "";

            p.addStatusCondition(this.status);

            if(this.status.equals(StatusCondition.BOUND)) this.duel.data(p.getUUID()).boundTurns = 5;

            if(this.status.equals(StatusCondition.CURSED))
            {
                Pokemon other = this.userChange ? this.opponent : this.user;
                other.damage(p.getStat(Stat.HP) / 2);

                return other.getName() + " sacrificed " + (p.getStat(Stat.HP) / 2) + " HP to curse " + p.getName() + "!";
            }

            if(this.status.equals(StatusCondition.NIGHTMARE) && !p.hasStatusCondition(StatusCondition.ASLEEP))
            {
                return this.move.getNoEffectResult(p);
            }

            if(this.status.equals(StatusCondition.BADLY_POISONED))
            {
                this.duel.data(opponent.getUUID()).badlyPoisonedTurns++;
            }

            if(this.status.equals(StatusCondition.FLINCHED) && this.duel.first.equals(p.getUUID()))
            {
                return p.getName() + " did not flinch!";
            }

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
