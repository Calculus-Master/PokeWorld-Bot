package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractTypeObjective;
import com.calculusmaster.pokecord.util.Global;

public class CatchTypeObjective extends AbstractTypeObjective
{
    public CatchTypeObjective()
    {
        super(ObjectiveType.CATCH_POKEMON_TYPE);
    }

    @Override
    public String getDesc()
    {
        return "Catch " + this.target + " Pokemon that are " + Global.normalize(this.type.toString()) + " Type";
    }
}
