package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;

import java.util.Random;

public class BugMoves
{
    public String SilverWind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage, duel);

        if(new Random().nextInt(100) < 10)
        {
            user.changeStatMultiplier(Stat.ATK, 1);
            user.changeStatMultiplier(Stat.DEF, 1);
            user.changeStatMultiplier(Stat.SPATK, 1);
            user.changeStatMultiplier(Stat.SPDEF, 1);

            return move.getDamageResult(opponent, damage) + " " + user.getName() + "'s Attack, Defense, Special Attack and Special Defense rose by 1 stage!";
        }

        return move.getDamageResult(opponent, damage);
    }

    public String BugBuzz(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage, duel);

        if(new Random().nextInt(100) < 10)
        {
            opponent.changeStatMultiplier(Stat.SPDEF, -1);

            return move.getDamageResult(opponent, damage) + " " + opponent.getName() + "'s Special Defense was lowered by 1 stage!";
        }

        return move.getDamageResult(opponent, damage);
    }

    public String RagePowder(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String QuiverDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.SPATK, 1);
        user.changeStatMultiplier(Stat.SPDEF, 1);
        user.changeStatMultiplier(Stat.SPD, 1);

        return user.getName() + "'s Special Attack, Special Defense and Speed rose by 1 stage";
    }

    public String Twineedle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        damage += move.getDamage(user, opponent);

        if(!opponent.getType()[0].equals(Type.STEEL) && !opponent.getType()[1].equals(Type.STEEL) && !opponent.getType()[0].equals(Type.POISON) && !opponent.getType()[1].equals(Type.POISON))
        {
            if(new Random().nextInt(100) < 20)
            {
                opponent.addStatusCondition(StatusCondition.POISONED);
                return move.getDamageResult(opponent, damage) + " " + opponent.getName() + " is poisoned!";
            }
        }

        return move.getDamageResult(opponent, damage);
    }

    public String PinMissile(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        int times = 1;

        Random r = new Random();

        if(r.nextInt(8) < 3)
        {
            move.setPower(50);
            damage += move.getDamage(user, opponent);
            times++;

            if(r.nextInt(8) < 3)
            {
                move.setPower(75);
                damage += move.getDamage(user, opponent);
                times++;

                if(r.nextInt(8) < 1)
                {
                    move.setPower(100);
                    damage += move.getDamage(user, opponent);
                    times++;

                    if(r.nextInt(8) < 1)
                    {
                        move.setPower(125);
                        damage += move.getDamage(user, opponent);
                        times++;
                    }
                }
            }
        }

        opponent.damage(damage, duel);

        return move.getDamageResult(opponent, damage) + " Pin Missile hit " + times + " time" + (times > 1 ? "s!" : "!");
    }

    public String FellStinger(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage, duel);

        if(damage > user.getHealth())
        {
            user.changeStatMultiplier(Stat.ATK, 3);
            return move.getDamageResult(opponent, damage) + " " + user.getName() + "'s Attack rose by 3 stages!";
        }

        return move.getDamageResult(opponent, damage);
    }
}
