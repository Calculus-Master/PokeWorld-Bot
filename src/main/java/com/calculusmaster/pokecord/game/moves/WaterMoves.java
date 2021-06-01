package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
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
        opponent.damage(damage);

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
        opponent.damage(damage);

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
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage);
    }

    public String RainDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return user.getName() + " caused a rain shower!";
    }

    public String HydroPump(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

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

    public String MuddyWater(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String OriginPulse(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String WaterSpout(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.setPower(150 * user.getHealth() / (double)user.getStat(Stat.HP));
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Dive(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(user.getUUID()).diveUsed)
        {
            duel.data(user.getUUID()).diveUsed = false;
            return Move.simpleDamageMove(user, opponent, duel, move);
        }
        else
        {
            duel.data(user.getUUID()).diveUsed = true;
            return user.getName() + " hid underwater!";
        }
    }

    public String Surf(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Whirlpool(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.addStatusCondition(StatusCondition.BOUND);
        return Move.simpleDamageMove(user, opponent, duel, move) + " " + opponent.getName() + " is bound!";
    }

    public String WaterShuriken(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        int times = 1;

        Random r = new Random();

        if(r.nextInt(8) < 3)
        {
            move.setPower(30);
            damage += move.getDamage(user, opponent);
            times++;

            if(r.nextInt(8) < 3)
            {
                move.setPower(45);
                damage += move.getDamage(user, opponent);
                times++;

                if(r.nextInt(8) < 1)
                {
                    move.setPower(60);
                    damage += move.getDamage(user, opponent);
                    times++;

                    if(r.nextInt(8) < 1)
                    {
                        move.setPower(75);
                        damage += move.getDamage(user, opponent);
                        times++;
                    }
                }
            }
        }

        opponent.damage(damage);

        return move.getDamageResult(opponent, damage) + " Water Shuriken hit " + times + " time" + (times > 1 ? "s!" : "!");
    }

    public String Scald(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.BURNED, 30);
    }
}
