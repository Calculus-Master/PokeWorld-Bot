package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractNameObjective;
import com.calculusmaster.pokecord.game.moves.Move;

public class UseMoveNameObjective extends AbstractNameObjective
{
    public UseMoveNameObjective()
    {
        super(ObjectiveType.USE_MOVES_NAME);
    }

    @Override
    protected void setRandomName()
    {
        this.name = Move.getRandomMove();
    }

    @Override
    public String getDesc()
    {
        return "Use the move \"" + this.name + "\" " + this.target + " times";
    }
}
