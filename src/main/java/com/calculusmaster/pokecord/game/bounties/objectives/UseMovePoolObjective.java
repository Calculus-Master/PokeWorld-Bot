package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractPoolObjective;
import com.calculusmaster.pokecord.game.moves.Move;

import java.util.Random;

public class UseMovePoolObjective extends AbstractPoolObjective
{
    public UseMovePoolObjective()
    {
        super(ObjectiveType.USE_MOVES_POOL);
    }

    @Override
    protected void setRandomPool()
    {
        int size = new Random().nextInt(15) + 5;
        for(int i = 0; i < size; i++) this.pool.add(Move.getRandomMove());
    }

    @Override
    public String getDesc()
    {
        return "Use any " + this.target + " moves from this list: " + this.pool;
    }
}
