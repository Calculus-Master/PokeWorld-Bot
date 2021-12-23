package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractTypeObjective;
import com.calculusmaster.pokecord.util.Global;

public class DamageTypeObjective extends AbstractTypeObjective
{
    public DamageTypeObjective()
    {
        super(ObjectiveType.DAMAGE_POKEMON_TYPE);
    }

    @Override
    public String getDesc()
    {
        return "Deal " + this.target + " damage to opponent Pokemon that are " + Global.normalize(this.type.toString()) + " Type";
    }
}
