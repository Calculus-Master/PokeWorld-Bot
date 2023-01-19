package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractTypeObjective;
import com.calculusmaster.pokecord.util.Global;

public class DefeatTypeObjective extends AbstractTypeObjective
{
    public DefeatTypeObjective()
    {
        super(ObjectiveType.DEFEAT_POKEMON_TYPE);
    }

    @Override
    public String getDesc()
    {
        return "Defeat any " + this.target + " Pokemon that are " + Global.normalize(this.type.toString()) + " Type";
    }
}
