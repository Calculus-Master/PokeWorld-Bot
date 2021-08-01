package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class DefeatLegendaryObjective extends Objective
{
    public DefeatLegendaryObjective()
    {
        super(ObjectiveType.DEFEAT_LEGENDARY);
    }

    @Override
    public String getDesc()
    {
        return "Defeat " + this.target + " Legendary, Mythical, or Ultra Beast Pokemon";
    }
}
