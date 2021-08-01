package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractTypeObjective;
import com.calculusmaster.pokecord.util.Global;

public class UseZMoveTypeObjective extends AbstractTypeObjective
{
    public UseZMoveTypeObjective()
    {
        super(ObjectiveType.USE_ZMOVE_TYPE);
    }

    @Override
    public String getDesc()
    {
        return "Use any " + this.target + " Z-Moves that are " + Global.normalCase(this.type.toString()) + " Type";
    }
}
