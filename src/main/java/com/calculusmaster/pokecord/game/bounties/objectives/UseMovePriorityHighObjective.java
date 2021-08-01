package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class UseMovePriorityHighObjective extends Objective
{
    public UseMovePriorityHighObjective()
    {
        super(ObjectiveType.USE_MOVES_PRIORITY_HIGH);
    }

    @Override
    public String getDesc()
    {
        return "Use any " + this.target + " moves with high priority";
    }
}
