package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;

public class ReleaseGenericObjective extends Objective
{
    public ReleaseGenericObjective()
    {
        super(ObjectiveType.RELEASE_POKEMON, Objective.randomTargetAmount(5, 30));
    }

    @Override
    public String getDesc()
    {
        return "Release " + this.target + " Pokemon";
    }
}
