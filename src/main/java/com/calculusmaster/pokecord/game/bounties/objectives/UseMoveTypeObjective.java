package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.util.Global;
import org.bson.Document;

import java.util.Random;

public class UseMoveTypeObjective extends Objective
{
    private Type type;

    public UseMoveTypeObjective()
    {
        super(ObjectiveType.USE_MOVES_TYPE, Objective.randomTargetAmount(10, 30));
        this.type = Type.values()[new Random().nextInt(Type.values().length)];
    }

    @Override
    public String getDesc()
    {
        return "Use any " + this.target + " Moves that are " + Global.normalCase(this.type.toString()) + " Type";
    }

    @Override
    public Document addObjectiveData(Document document)
    {
        return super.addObjectiveData(document)
                .append("type", this.type.toString());
    }

    public Type getType()
    {
        return this.type;
    }

    public UseMoveTypeObjective setType(String type)
    {
        this.type = Type.cast(type);
        return this;
    }
}
