package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;

public class EarnPokemonXPObjective extends Objective
{
    public EarnPokemonXPObjective()
    {
        super(ObjectiveType.EARN_XP_POKEMON, Objective.randomTargetAmount(2000, 16000));
    }

    @Override
    public String getDesc()
    {
        return "Earn " + this.target + " Pokemon EXP";
    }
}
