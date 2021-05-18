package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;

public class GrassMoves
{
    public String RazorLeaf(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.setCrit(3);

        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        user.setCrit(1);

        return move.getDamageResult(opponent, damage);
    }

    public String VineWhip(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage);
    }

    public String LeechSeed(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //TODO: LeechSeed sucks health each turn, right now its coded to steal max health once

        if(!opponent.getType()[0].equals(Type.GRASS) && !opponent.getType()[1].equals(Type.GRASS))
        {
            int hpGain = opponent.getStat(Stat.HP) / 8;

            user.heal(hpGain);
            opponent.damage(hpGain);

            return user.getName() + " leeched " + hpGain + " HP from " + opponent.getName() + "!";
        }
        else return move.getNoEffectResult(opponent);
    }

    public String SleepPowder(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.setStatusCondition(StatusCondition.ASLEEP);

        return opponent.getName() + "is asleep!";
    }

    public String SeedBomb(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage);
    }

    public String Synthesis(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int healAmount = user.getStat(Stat.HP);

        switch (duel.getDuelWeather())
        {
            case CLEAR -> healAmount /= 2;
            case HARSH_SUNLIGHT -> healAmount = healAmount * 2 / 3;
            default -> healAmount /= 4;
        }

        user.heal(healAmount);

        return user.getName() + " healed " + healAmount + " HP!";
    }
}
