package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class SwapGenericObjective extends Objective
{
    public SwapGenericObjective()
    {
        super(ObjectiveType.SWAP_POKEMON);
    }

    @Override
    public String getDesc()
    {
        return "Swap out Pokemon in duels " + this.target + " times";
    }
}
