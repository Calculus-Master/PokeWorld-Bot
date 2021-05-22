package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;

import java.util.Random;

public class IceMoves
{
    public String Hail(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return user.getName() + " summoned a hailstorm!";
    }

    public String IceBall(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String IcicleCrash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(new Random().nextInt(100) < 30)
        {
            opponent.addStatusCondition(StatusCondition.FLINCHED);
            return Move.simpleDamageMove(user, opponent, duel, move) + " " + opponent.getName() + " flinched!";
        }

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String IcicleSpear(Pokemon user, Pokemon opponent, Duel duel, Move move)
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

        return move.getDamageResult(opponent, damage) + " Icicle Spear hit " + times + " time" + (times > 1 ? "s!" : "!");
    }

    public String IceBeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        boolean freeze = new Random().nextInt(100) < 10;

        if(freeze) opponent.addStatusCondition(StatusCondition.FROZEN);

        return Move.simpleDamageMove(user, opponent, duel, move) + (freeze ? " " + opponent.getName() + " is now frozen!" : "");
    }

    public String Blizzard(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        boolean freeze = new Random().nextInt(100) < 10;

        if(freeze) opponent.addStatusCondition(StatusCondition.FROZEN);

        return Move.simpleDamageMove(user, opponent, duel, move) + (freeze ? " " + opponent.getName() + " is now frozen!" : "");
    }
}
