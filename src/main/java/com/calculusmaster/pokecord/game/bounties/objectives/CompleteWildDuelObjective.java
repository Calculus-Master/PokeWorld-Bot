package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class CompleteWildDuelObjective extends Objective
{
    public CompleteWildDuelObjective()
    {
        super(ObjectiveType.COMPLETE_WILD_DUEL);
    }

    @Override
    public String getDesc()
    {
        return "Complete " + this.target + " Wild Duels";
    }
}
