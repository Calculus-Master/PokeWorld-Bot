package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class EarnPokePassXPObjective extends Objective
{
    public EarnPokePassXPObjective()
    {
        super(ObjectiveType.EARN_XP_POKEPASS);
    }

    @Override
    public String getDesc()
    {
        return "Earn " + this.target + " PokePass EXP";
    }
}
