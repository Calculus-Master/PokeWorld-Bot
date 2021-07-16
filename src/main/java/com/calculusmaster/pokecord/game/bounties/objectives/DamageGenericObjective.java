package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class DamageGenericObjective extends Objective
{
    public DamageGenericObjective()
    {
        super(ObjectiveType.DAMAGE_POKEMON);
    }

    @Override
    public String getDesc()
    {
        return "Deal " + this.target + " damage to opponent Pokemon";
    }
}
