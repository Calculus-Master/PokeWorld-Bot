package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class BreedGenericObjective extends Objective
{
    public BreedGenericObjective()
    {
        super(ObjectiveType.BREED_POKEMON);
    }

    @Override
    public String getDesc()
    {
        return "Breed " + this.target + " Pokemon";
    }
}
