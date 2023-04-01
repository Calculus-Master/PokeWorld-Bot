package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractNameObjective;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;

public class UseMoveNameObjective extends AbstractNameObjective
{
    public UseMoveNameObjective()
    {
        super(ObjectiveType.USE_MOVES_NAME);
    }

    @Override
    protected void setRandomName()
    {
        this.entityName = MoveEntity.getRandom().data().getName();
    }

    @Override
    public String getDesc()
    {
        return "Use the move \"" + MoveEntity.cast(this.entityName) + "\" " + this.target + " times";
    }
}
