package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;

public class EarnPokePassXPObjective extends Objective
{
    public EarnPokePassXPObjective()
    {
        super(ObjectiveType.EARN_XP_POKEPASS, Objective.randomTargetAmount(1000, 15000));
    }

    @Override
    public String getDesc()
    {
        return "Earn " + this.target + " PokePass EXP";
    }
}
