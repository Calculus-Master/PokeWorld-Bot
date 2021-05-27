package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;

public class DarkMoves
{
    public String Bite(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FLINCHED, 30);
    }

    public String Pursuit(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Assurance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(user.getUUID()).lastDamageTaken > 0) move.setPower(move.getPower() * 2);

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Taunt(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).tauntTurns = 3;
        return opponent.getName() + " is now unable to use Status moves for 3 turns!";
    }

    public String Payback(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(user.getStat(Stat.SPD) < opponent.getStat(Stat.SPD)) move.setPower(move.getPower() * 2);

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String SuckerPunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String FieryWrath(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FLINCHED, 20);
    }

    public String NastyPlot(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.SPATK, 2);

        return user.getName() + "'s Special Attack rose by 2 stages!";
    }

    public String Memento(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.changeStatMultiplier(Stat.ATK, -3);
        opponent.changeStatMultiplier(Stat.SPATK, -3);

        user.damage(user.getHealth());

        return user.getName() + " fainted and " + opponent.getName() + "'s Attack and Special Attack lowered by 3 stages!";
    }

    public String NightSlash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.setCrit(3);

        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        user.setCrit(1);

        return move.getDamageResult(opponent, damage);
    }

    public String DarkPulse(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FLINCHED, 20);
    }

    public String Crunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.DEF, -1, 20, false);
    }

    public String Punishment(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int statIncreases = 0;

        for(Stat s : Stat.values()) if(opponent.getStatMultiplier(s) > 0) statIncreases += (int)(opponent.getStatMultiplier(s) * 2 - 2);

        move.setPower(statIncreases * 20 + 60);

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String KnockOff(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.hasItem())
        {
            move.setPower(1.5 * move.getPower());
            opponent.removeItem();
        }

        return Move.simpleDamageMove(user, opponent, duel, move);
    }
}
