package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractTypeObjective;
import com.calculusmaster.pokecord.util.Global;

public class UseMaxMoveTypeObjective extends AbstractTypeObjective
{
    public UseMaxMoveTypeObjective()
    {
        super(ObjectiveType.USE_MAX_MOVE_TYPE);
    }

    @Override
    public String getDesc()
    {
        return "Use any " + this.target + " Max Moves that are " + Global.normalize(this.type.toString()) + " Type";
    }
}
