package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import org.bson.Document;

import java.util.List;
import java.util.Random;

public class UseMovePoolObjective extends Objective
{
    private List<String> pool;

    public UseMovePoolObjective()
    {
        super(ObjectiveType.USE_MOVES_POOL, Objective.randomTargetAmount(10, 50));

        int size = new Random().nextInt(15) + 5;
        for(int i = 0; i < size; i++) this.pool.add(Move.getRandomMove());
    }

    @Override
    public String getDesc()
    {
        return "Use any " + this.target + " moves from this list: " + this.pool;
    }

    @Override
    public Document addObjectiveData(Document document)
    {
        return super.addObjectiveData(document)
                .append("pool", this.pool);
    }

    public List<String> getPool()
    {
        return this.pool;
    }

    public UseMovePoolObjective setPool(List<String> pool)
    {
        this.pool = pool;
        return this;
    }
}
