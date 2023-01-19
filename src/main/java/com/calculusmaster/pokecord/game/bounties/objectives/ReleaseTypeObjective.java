package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractTypeObjective;
import com.calculusmaster.pokecord.util.Global;

public class ReleaseTypeObjective extends AbstractTypeObjective
{
    public ReleaseTypeObjective()
    {
        super(ObjectiveType.RELEASE_POKEMON_TYPE);
    }

    @Override
    public String getDesc()
    {
        return "Release " + this.target + " Pokemon that are " + Global.normalize(this.type.toString()) + " Type";
    }
}
