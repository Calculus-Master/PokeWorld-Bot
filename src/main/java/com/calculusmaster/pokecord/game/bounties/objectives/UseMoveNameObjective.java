package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import org.bson.Document;

public class UseMoveNameObjective extends Objective
{
    private String name;

    public UseMoveNameObjective()
    {
        super(ObjectiveType.USE_MOVES_NAME, Objective.randomTargetAmount(2, 10));
        this.name = Move.getRandomMove();
    }

    @Override
    public String getDesc()
    {
        return "Use the move \"" + this.name + "\" " + this.target + " times";
    }

    @Override
    public Document addObjectiveData(Document document)
    {
        return super.addObjectiveData(document)
                .append("name", this.name);
    }

    public String getName()
    {
        return this.name;
    }

    public UseMoveNameObjective setName(String name)
    {
        this.name = name;
        return this;
    }
}
