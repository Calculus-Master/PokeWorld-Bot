package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.MoveNew;
import com.calculusmaster.pokecord.game.Pokemon;

public class DragonMoves
{
    public String DragonClaw(Pokemon user, Pokemon opponent, Duel duel, MoveNew move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);
        return move.getDamageResult(opponent, damage);
    }
}
