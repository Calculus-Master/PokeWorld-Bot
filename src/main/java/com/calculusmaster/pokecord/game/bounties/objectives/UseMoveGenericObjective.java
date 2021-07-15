package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class UseMoveGenericObjective extends Objective
{
    public UseMoveGenericObjective()
    {
        super(ObjectiveType.USE_MOVES);
    }

    @Override
    public String getDesc()
    {
        return "Use any " + this.target + " moves";
    }
}
