package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.enums.elements.FieldBarrier;
import com.calculusmaster.pokecord.game.enums.elements.Gender;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.items.Item;
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

            if(this.duel.barriers[this.duel.playerIndexFromUUID(p.getUUID())].has(FieldBarrier.SAFEGUARD))
            {
                return p.getName() + " was protected by its Safeguard Barrier!";
            }

            if(this.status.equals(StatusCondition.INFATUATED) &&
                    ((this.user.getGender().equals(Gender.UNKNOWN)) || this.opponent.getGender().equals(Gender.UNKNOWN)) ||
                    this.user.getGender().equals(this.opponent.getGender()))
                return p.getName() + " was not infatuated!";

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

            if(this.status.equals(StatusCondition.INFATUATED) && p.getItem().equals(Item.DESTINY_KNOT))
            {
                Pokemon other = this.userChange ? this.opponent : this.user;
                other.addStatusCondition(StatusCondition.INFATUATED);
                return p.getName() + " is now infatuated! Due to the Destiny Knot, " + other.getName() + " is also now infatuated!";
            }

            return p.getName() + " " + switch(this.status) {
                case BURNED -> "is now burned!";
                case FROZEN -> "is now frozen!";
                case PARALYZED -> "is now paralyzed!";
                case ASLEEP -> "is now asleep!";
                case CONFUSED -> "is now confused!";
                case POISONED -> "is now poisoned!";
                case FLINCHED -> "flinched!";
                case CURSED -> "is now cursed!";
                case NIGHTMARE -> "has been afflicted with a Nightmare!";
                case BOUND -> "is now bound!";
                case BADLY_POISONED -> "is now badly poisoned!";
                case INFATUATED -> "is now infatuated!";
            };
        }
        else return "";
    }
}
