package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class CatchGenericObjective extends Objective
{
    public CatchGenericObjective()
    {
        super(ObjectiveType.CATCH_POKEMON);
    }

    @Override
    public String getDesc()
    {
        return "Catch " + this.target + " Pokemon";
    }
}
