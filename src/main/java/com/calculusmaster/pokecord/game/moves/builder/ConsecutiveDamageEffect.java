package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.enums.elements.Ability;

import java.util.Arrays;
import java.util.List;

public class ConsecutiveDamageEffect extends FixedDamageEffect
{
    private List<Integer> powers;

    public ConsecutiveDamageEffect(int... powers)
    {
        this.powers = Arrays.stream(powers).boxed().toList();
    }

    @Override
    public String get()
    {
        int totalDamage = this.move.getDamage(this.user, this.opponent);
        int hits = 1;

        for(int power : this.powers.subList(1, this.powers.size()))
        {
            this.move.setPower(power);

            if(this.user.hasAbility(Ability.MOXIE) || this.move.isAccurate(this.user, this.opponent))
            {
                totalDamage += this.move.getDamage(this.user, this.opponent);
                hits++;
            }
            else break;
        }

        this.move.setPower(this.powers.get(0));

        this.set(totalDamage);
        return super.get() + " " + this.user.getName() + " landed " + hits + " hits!";
    }
}
