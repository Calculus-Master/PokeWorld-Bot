package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.duel.Duel;

public abstract class MoveEffect
{
    protected Pokemon user, opponent;
    protected Duel duel;
    protected Move move;

    public void init(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        this.user = user;
        this.opponent = opponent;
        this.duel = duel;
        this.move = move;
    }

    public abstract String get();
}
