package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class ReleaseGenericObjective extends Objective
{
    public ReleaseGenericObjective()
    {
        super(ObjectiveType.RELEASE_POKEMON);
    }

    @Override
    public String getDesc()
    {
        return "Release " + this.target + " Pokemon";
    }
}
