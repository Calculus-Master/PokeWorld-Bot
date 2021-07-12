package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;

public class EvolvePokemonObjective extends Objective
{
    public EvolvePokemonObjective()
    {
        super(ObjectiveType.EVOLVE_POKEMON, Objective.randomTargetAmount(1, 3));
    }

    @Override
    public String getDesc()
    {
        return "Evolve " + this.target + " Pokemon";
    }
}
