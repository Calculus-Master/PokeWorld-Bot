package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;

import java.util.Random;

public class WaterMoves
{
    public String WaterGun(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Withdraw(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.DEF, 1);

        return user.getName() + "'s Defense rose by 1 stage!";
    }

    public String Bubble(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage, duel);

        if(new Random().nextInt(100) < 10)
        {
            opponent.changeStatMultiplier(Stat.SPD, -1);

            return move.getDamageResult(opponent, damage) + " " + opponent.getName() + "'s Speed was lowered by 1 stage!";
        }

        return move.getDamageResult(opponent, damage);
    }

    public String WaterPulse(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage, duel);

        if(new Random().nextInt(100) < 20)
        {
            opponent.addStatusCondition(StatusCondition.CONFUSED);
            return move.getDamageResult(opponent, damage) + " " + opponent.getName() + " is confused!";
        }

        return move.getDamageResult(opponent, damage);
    }

    public String AquaTail(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage, duel);

        return move.getDamageResult(opponent, damage);
    }

    public String RainDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return user.getName() + " caused a rain shower!";
    }

    public String HydroPump(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage, duel);

        return move.getDamageResult(opponent, damage);
    }

    public String HydroCannon(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String AquaRing(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int heal = user.getStat(Stat.HP) / 16;

        user.heal(heal);

        return user.getName() + " healed for " + heal + " HP!";
    }
}
