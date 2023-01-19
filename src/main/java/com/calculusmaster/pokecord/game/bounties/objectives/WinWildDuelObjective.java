package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class WinWildDuelObjective extends Objective
{
    public WinWildDuelObjective()
    {
        super(ObjectiveType.WIN_WILD_DUEL);
    }

    @Override
    public String getDesc()
    {
        return "Win " + this.target + " Wild Pokemon Duels";
    }
}
