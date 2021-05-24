package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;

import java.util.Random;

public class RockMoves
{
    public String Rollout(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Sandstorm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return user.getName() + " caused a sandstorm!";
    }

    public String SmackDown(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //TODO: Raised Pokemon
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
}
