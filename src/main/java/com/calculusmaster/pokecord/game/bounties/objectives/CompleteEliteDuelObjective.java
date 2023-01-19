package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class CompleteEliteDuelObjective extends Objective
{
    public CompleteEliteDuelObjective()
    {
        super(ObjectiveType.COMPLETE_ELITE_DUEL);
    }

    @Override
    public String getDesc()
    {
        return "Complete " + this.target + " Elite Trainer Duels";
    }
}
