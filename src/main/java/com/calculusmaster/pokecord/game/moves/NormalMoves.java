package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.MoveNew;
import com.calculusmaster.pokecord.game.Pokemon;

public class NormalMoves
{
    public String Tackle(Pokemon user, Pokemon opponent, Duel duel, MoveNew move)
    {
        if(2 > 1) return "Hi!";

        int damage = move.getDamage(user, opponent);
        opponent.changeHealth(damage * -1);

        return move.getBasicResult(user, opponent, damage);
    }
}
