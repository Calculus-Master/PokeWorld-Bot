package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

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

    //Lower values mean they go first
    public int getPriority()
    {
        if(this instanceof PowerBoostEffect) return 0;
        else if(this instanceof FixedDamageEffect) return 1;
        else if(this instanceof FixedHealEffect || this instanceof RecoilEffect) return 2;
        else if(this instanceof StatChangeEffect) return 3;
        else if(this instanceof StatusConditionEffect) return 4;
        else return 100;
    }
}
