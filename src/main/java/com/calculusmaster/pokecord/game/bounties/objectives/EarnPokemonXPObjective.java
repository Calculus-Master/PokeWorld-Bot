package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class EarnPokemonXPObjective extends Objective
{
    public EarnPokemonXPObjective()
    {
        super(ObjectiveType.EARN_XP_POKEMON);
    }

    @Override
    public String getDesc()
    {
        return "Earn " + this.target + " Pokemon EXP";
    }
}
