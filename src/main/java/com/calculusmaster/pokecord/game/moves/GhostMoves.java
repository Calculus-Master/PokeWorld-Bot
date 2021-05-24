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
}
