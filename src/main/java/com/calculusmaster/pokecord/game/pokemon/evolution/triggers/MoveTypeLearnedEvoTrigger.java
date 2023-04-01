package com.calculusmaster.pokecord.game.pokemon.evolution.triggers;

import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public class MoveTypeLearnedEvoTrigger implements EvolutionTrigger
{
    private final Type type;

    public MoveTypeLearnedEvoTrigger(Type type)
    {
        this.type = type;
    }

    @Override
    public boolean canEvolve(Pokemon p, String serverID)
    {
        return p.getMoves().stream().map(e -> e.data().getType()).anyMatch(this.type::equals);
    }

    @Override
    public String getDescription()
    {
        return "Has learned a " + this.type.getStyledName() + "-Type move";
    }
}
