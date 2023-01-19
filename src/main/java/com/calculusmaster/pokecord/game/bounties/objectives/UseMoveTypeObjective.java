package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractTypeObjective;
import com.calculusmaster.pokecord.util.Global;

public class UseMoveTypeObjective extends AbstractTypeObjective
{
    public UseMoveTypeObjective()
    {
        super(ObjectiveType.USE_MOVES_TYPE);
    }

    @Override
    public String getDesc()
    {
        return "Use any " + this.target + " Moves that are " + Global.normalize(this.type.toString()) + " Type";
    }
}
