package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class CompleteGauntletLevelsObjective extends Objective
{
    public CompleteGauntletLevelsObjective()
    {
        super(ObjectiveType.COMPLETE_GAUNTLET_LEVELS);
    }

    @Override
    public String getDesc()
    {
        return "Complete " + this.target + " levels in Gauntlet Duels";
    }
}
