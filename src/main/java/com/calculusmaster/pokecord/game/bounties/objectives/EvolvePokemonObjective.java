package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class EvolvePokemonObjective extends Objective
{
    public EvolvePokemonObjective()
    {
        super(ObjectiveType.EVOLVE_POKEMON);
    }

    @Override
    public String getDesc()
    {
        return "Evolve " + this.target + " Pokemon";
    }
}
