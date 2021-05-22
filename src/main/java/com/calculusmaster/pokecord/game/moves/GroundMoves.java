package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;

public class GroundMoves
{
    public String Earthquake(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //TODO: Double damage if opponent has used Dig
        return Move.simpleDamageMove(user, opponent, duel, move);
    }
}
