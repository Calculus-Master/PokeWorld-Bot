package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class DefeatGenericObjective extends Objective
{
    public DefeatGenericObjective()
    {
        super(ObjectiveType.DEFEAT_POKEMON);
    }

    @Override
    public String getDesc()
    {
        return "Defeat any " + this.target + " Pokemon";
    }
}
