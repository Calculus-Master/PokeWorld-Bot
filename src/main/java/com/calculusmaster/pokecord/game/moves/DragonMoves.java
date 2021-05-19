package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;

import java.util.Random;

public class DragonMoves
{
    public String DragonClaw(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage, duel);
        return move.getDamageResult(opponent, damage);
    }

    public String DragonRage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.damage(40, duel);

        return move.getDamageResult(opponent, 40);
    }

    public String DragonBreath(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage, duel);

        if(!opponent.getType()[0].equals(Type.ELECTRIC) && !opponent.getType()[1].equals(Type.ELECTRIC) && new Random().nextInt(100) < 30)
        {
            opponent.setStatusCondition(StatusCondition.PARALYZED);
            return move.getDamageResult(opponent, damage) + " " + opponent.getName() + " is paralyzed!";
        }

        return move.getDamageResult(opponent, damage);
    }
}
