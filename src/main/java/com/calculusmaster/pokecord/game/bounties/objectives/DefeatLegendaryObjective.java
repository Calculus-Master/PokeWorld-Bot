package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;

public class DefeatLegendaryObjective extends Objective
{
    public DefeatLegendaryObjective()
    {
        super(ObjectiveType.DEFEAT_LEGENDARY, Objective.randomTargetAmount(1, 3));
    }

    @Override
    public String getDesc()
    {
        return "Defeat " + this.target + " Legendary, Mythical, or Ultra Beast Pokemon";
    }
}
