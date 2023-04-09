package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.List;
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
        if(this.user.hasAbility(Ability.SERENE_GRACE))
        {
            this.percent *= 2;

            //TODO: Serene Grace result
            //result.append(Ability.SERENE_GRACE.formatActivation(this.user.getName(), this.move.getName() + " has been graced!")).append(" ");
        }

        if(new Random().nextInt(100) < this.percent)
        {
            Pokemon p = this.userChange ? this.user : this.opponent;

            if(p.hasStatusCondition(this.status)) return "";

            if(this.duel.barriers[this.duel.playerIndexFromUUID(p.getUUID())].has(FieldBarrier.SAFEGUARD))
            {
                return p.getName() + " was protected by its Safeguard Barrier!";
            }

            if(this.status.equals(StatusCondition.INFATUATED))
            {
                boolean userUnknown = this.user.getGender().equals(Gender.UNKNOWN);
                boolean opponentUnknown = this.opponent.getGender().equals(Gender.UNKNOWN);
                boolean equalGender = this.user.getGender().equals(this.opponent.getGender());

                if(userUnknown || opponentUnknown || equalGender) return p.getName() + " was not infatuated!";
            }

            if(p.hasAbility(Ability.LIMBER) && this.status.equals(StatusCondition.PARALYZED))
                return Ability.LIMBER.formatActivation(p.getName(), p.getName() + " was not paralyzed!");

            if(p.hasAbility(Ability.PASTEL_VEIL) && (this.status.equals(StatusCondition.POISONED) || this.status.equals(StatusCondition.BADLY_POISONED)))
                return Ability.PASTEL_VEIL.formatActivation(p.getName(), p.getName() + " was not poisoned!");

            if(p.hasAbility(Ability.WATER_VEIL) && this.status.equals(StatusCondition.BURNED))
                return Ability.WATER_VEIL.formatActivation(p.getName(), p.getName() + " was immune to the burn!");

            if(p.hasAbility(Ability.VITAL_SPIRIT) && this.status.equals(StatusCondition.ASLEEP))
                return Ability.VITAL_SPIRIT.formatActivation(p.getName(), p.getName() + " did not fall asleep!");

            if(p.hasAbility(Ability.AROMA_VEIL) && this.status.equals(StatusCondition.INFATUATED))
                return Ability.AROMA_VEIL.formatActivation(p.getName(), p.getName() + " was not affected by the infatuation!");

            if(!this.userChange && p.hasAbility(Ability.LEAF_GUARD) && (this.duel.weather.get().equals(Weather.HARSH_SUNLIGHT) || this.duel.weather.get().equals(Weather.EXTREME_HARSH_SUNLIGHT)) && List.of(StatusCondition.ASLEEP, StatusCondition.BURNED, StatusCondition.PARALYZED, StatusCondition.POISONED, StatusCondition.BADLY_POISONED, StatusCondition.FROZEN).contains(this.status))
                return Ability.LEAF_GUARD.formatActivation(p.getName(), p.getName() + " was protected from the Status Condition!");

            if(p.hasAbility(Ability.MAGMA_ARMOR) && this.status.equals(StatusCondition.FROZEN))
                return Ability.MAGMA_ARMOR.formatActivation(p.getName(), p.getName() + " cannot be frozen!");

            if(p.hasAbility(Ability.INNER_FOCUS) && this.status.equals(StatusCondition.FLINCHED))
                return Ability.INNER_FOCUS.formatActivation(p.getName(), p.getName() + " did not flinch!");

            if((this.status.equals(StatusCondition.POISONED) || this.status.equals(StatusCondition.BADLY_POISONED)) && !this.userChange)
            {
                boolean poisonImmune = p.isType(Type.STEEL) || p.isType(Type.POISON);
                boolean corrosionOverride = poisonImmune && p.hasAbility(Ability.CORROSION) && this.percent == 100;

                if(poisonImmune && !corrosionOverride) return p.getName() + " was immune to the poison!";
            }

            if(this.status.equals(StatusCondition.FLINCHED) && this.duel.first.equals(p.getUUID()))
                return "The flinch had no effect since " + p.getName() + " moved first!";

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
