package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;

import java.util.Random;

public class PoisonMoves
{
    //TODO: Badly Poisoned
    public String Toxic(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.getStatusCondition().equals(StatusCondition.NORMAL)) opponent.setStatusCondition(StatusCondition.POISONED);

        return opponent.getName() + " was poisoned!";
    }

    public String Venoshock(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);

        if(opponent.getStatusCondition().equals(StatusCondition.POISONED)) damage *= 2;

        opponent.damage(damage, duel);

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

    public String ToxicSpikes(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Toxic(user, opponent, duel, move);
    }

    public String PoisonJab(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage, duel);

        if(new Random().nextInt(100) < 30)
        {
            opponent.setStatusCondition(StatusCondition.POISONED);

            return move.getDamageResult(opponent, damage) + " " + opponent.getName() + " was poisoned!";
        }

        return move.getDamageResult(opponent, damage);
    }
}
