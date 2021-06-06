package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Weather;

import java.util.Random;

public class RockMoves
{
    public String Rollout(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Sandstorm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.weather = Weather.SANDSTORM;
        duel.weatherTurns = 5;

        return user.getName() + " caused a sandstorm!";
    }

    public String SmackDown(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(opponent.getUUID()).isRaised = false;
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String AncientPower(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        String raise = "";

        if(new Random().nextInt(100) < 10)
        {
            user.changeStatMultiplier(Stat.ATK, 1);
            user.changeStatMultiplier(Stat.DEF, 1);
            user.changeStatMultiplier(Stat.SPATK, 1);
            user.changeStatMultiplier(Stat.SPDEF, 1);
            user.changeStatMultiplier(Stat.SPD, 1);

            raise = " All of " + user.getName() + "'s stats rose by 1 stage!";
        }

        return Move.simpleDamageMove(user, opponent, duel, move) + raise;
    }

    public String PowerGem(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String RockBlast(Pokemon user, Pokemon opponent, Duel duel, Move move)
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

        opponent.damage(damage);

        return move.getDamageResult(opponent, damage) + " Rock Blast hit " + times + " time" + (times > 1 ? "s!" : "!");
    }

    public String StealthRock(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.hazardData(opponent.getUUID()).addHazard(DuelHelper.EntryHazard.STEALTH_ROCK);
        return user.getName() + " laid a Stealth Rock trap!";
    }

    public String RockThrow(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String RockTomb(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.changeStatMultiplier(Stat.SPD, -1);
        return Move.simpleDamageMove(user, opponent, duel, move) + " " + opponent.getName() + "'s Speed was lowered by 1 stage!";
    }

    public String RockSlide(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FLINCHED, 30);
    }

    public String StoneEdge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.setCrit(3);

        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        user.setCrit(1);

        return move.getDamageResult(opponent, damage);
    }

    public String DiamondStorm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.DEF, 2, 50, true);
    }

    public String WideGuard(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }
}
