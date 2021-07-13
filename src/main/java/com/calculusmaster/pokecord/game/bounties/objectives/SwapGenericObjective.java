package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;

public class SwapGenericObjective extends Objective
{
    public SwapGenericObjective()
    {
        super(ObjectiveType.SWAP_POKEMON, Objective.randomTargetAmount(5, 30));
    }

    @Override
    public String getDesc()
    {
        return "Swap out Pokemon in duels " + this.target + " times";
    }
}
