package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;

public class PoisonMoves
{
    public String Toxic(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.getStatusCondition().equals(StatusCondition.NORMAL)) opponent.setStatusCondition(StatusCondition.POISONED);

        return opponent.getName() + " was poisoned!";
    }

    public String Venoshock(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);

        if(opponent.getStatusCondition().equals(StatusCondition.POISONED)) damage *= 2;

        opponent.damage(damage);

        return move.getDamageResult(opponent, damage);
    }

    public String PoisonPowder(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(!opponent.getType()[0].equals(Type.POISON) && !opponent.getType()[1].equals(Type.POISON) && !opponent.getType()[0].equals(Type.STEEL) && !opponent.getType()[1].equals(Type.STEEL))
        {
            opponent.setStatusCondition(StatusCondition.POISONED);
            return opponent.getName() + " is poisoned!";
        }
        else return move.getNoEffectResult(opponent);
    }
}
