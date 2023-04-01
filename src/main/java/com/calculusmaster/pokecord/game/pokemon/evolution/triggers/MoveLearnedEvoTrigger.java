package com.calculusmaster.pokecord.game.pokemon.evolution.triggers;

import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public class MoveLearnedEvoTrigger implements EvolutionTrigger
{
    private final MoveEntity move;

    public MoveLearnedEvoTrigger(MoveEntity move)
    {
        this.move = move;
    }

    @Override
    public boolean canEvolve(Pokemon p, String serverID)
    {
        return p.getMoves().contains(this.move);
    }

    @Override
    public String getDescription()
    {
        return "Has learned the move " + this.move.data().getName();
    }
}
