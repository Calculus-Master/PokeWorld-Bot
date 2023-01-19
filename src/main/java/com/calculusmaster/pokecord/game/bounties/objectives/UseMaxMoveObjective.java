package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class UseMaxMoveObjective extends Objective
{
    public UseMaxMoveObjective()
    {
        super(ObjectiveType.USE_MAX_MOVE);
    }

    @Override
    public String getDesc()
    {
        return "Use " + this.target + " Max Moves";
    }
}
