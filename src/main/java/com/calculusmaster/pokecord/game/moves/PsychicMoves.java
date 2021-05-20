package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;

import java.util.Random;

public class PsychicMoves
{
    public String CalmMind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.SPATK, 1);
        user.changeStatMultiplier(Stat.SPDEF, 1);

        return "It raised " + user.getName() + "'s Special Attack and Special Defense by 1 stage!";
    }

    public String Psyshock(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage, duel);

        return move.getDamageResult(opponent, damage);
    }

    public String Confusion(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage, duel);

        if(new Random().nextInt(100) < 10)
        {
            opponent.setStatusCondition(StatusCondition.CONFUSED);

            return move.getDamageResult(opponent, damage) + " " + opponent.getName() + " is confused!";
        }

        return move.getDamageResult(opponent, damage);
    }

    public String Psybeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Confusion(user, opponent, duel, move);
    }
}
