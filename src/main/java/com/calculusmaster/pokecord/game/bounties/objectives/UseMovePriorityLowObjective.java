package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class UseMovePriorityLowObjective extends Objective
{
    public UseMovePriorityLowObjective()
    {
        super(ObjectiveType.USE_MOVES_PRIORITY_LOW);
    }

    @Override
    public String getDesc()
    {
        return "Use any " + this.target + " moves with low priority";
    }
}
