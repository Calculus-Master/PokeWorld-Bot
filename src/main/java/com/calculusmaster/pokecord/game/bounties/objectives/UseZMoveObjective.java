package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class UseZMoveObjective extends Objective
{
    public UseZMoveObjective()
    {
        super(ObjectiveType.USE_ZMOVE);
    }

    @Override
    public String getDesc()
    {
        return "Use " + this.target + " Z-Moves";
    }
}
