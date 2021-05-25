package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;

import java.util.Random;

public class GhostMoves
{
    public String ShadowBall(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        boolean lower = new Random().nextInt(100) < 20;

        if(lower) opponent.changeStatMultiplier(Stat.SPDEF, -1);

        return Move.simpleDamageMove(user, opponent, duel, move) + (lower ? " " + opponent.getName() + "'s Special Defense was lowered by 1 stage!" : "");
    }

    public String Curse(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(user.isType(Type.GHOST))
        {
            opponent.addStatusCondition(StatusCondition.CURSED);
            return opponent.getName() + " is cursed!";
        }
        else
        {
            user.changeStatMultiplier(Stat.ATK, 1);
            user.changeStatMultiplier(Stat.DEF, 1);
            user.changeStatMultiplier(Stat.SPD, -1);

            return user.getName() + "s Attack and Defense rose by 1 stage and Speed lowered by 1 stage!";
        }
    }

    public String OminousWind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        boolean raise = new Random().nextInt(100) < 10;

        if(raise)
        {
            user.changeStatMultiplier(Stat.ATK, 1);
            user.changeStatMultiplier(Stat.DEF, 1);
            user.changeStatMultiplier(Stat.SPATK, 1);
            user.changeStatMultiplier(Stat.SPDEF, 1);
            user.changeStatMultiplier(Stat.SPD, 1);
        }

        return Move.simpleDamageMove(user, opponent, duel, move) + (raise ? " " + user.getName() + "'s stats all rose by 1 stage!" : "");
    }

    public String ShadowSneak(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String DestinyBond(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String ShadowClaw(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.setCrit(3);
        int damage = move.getDamage(user, opponent);
        user.setCrit(1);
        opponent.damage(damage, duel);

        return move.getDamageResult(opponent, damage);
    }

    public String ShadowForce(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Hex(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(!opponent.getActiveStatusConditions().equals("")) move.setPower(130);

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Lick(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.PARALYZED, 30);
    }

    public String ShadowPunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Spite(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String NightShade(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.damage(user.getLevel(), duel);
        return move.getDamageResult(opponent, user.getLevel());
    }

    public String Nightmare(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.hasStatusCondition(StatusCondition.ASLEEP)) opponent.addStatusCondition(StatusCondition.NIGHTMARE);
        return user.getName() + " cast a Nightmare on " + opponent.getName() + "!";
    }

    public String SpectralThief(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.setDefaultStatMultipliers();
        int change;

        for(Stat s : Stat.values())
        {
            change = (int)(2 * opponent.getStatMultiplier(s))  + (opponent.getStat(s) > 0 ? -2 : 2);
            user.changeStatMultiplier(s, change);
        }

        return Move.simpleDamageMove(user, opponent, duel, move) + " " + user.getName() + " transferred all Status Conditions to " + opponent.getName() + "!";
    }

    public String MoongeistBeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }
}
