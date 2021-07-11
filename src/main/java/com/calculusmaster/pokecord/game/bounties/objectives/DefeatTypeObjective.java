package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.util.Global;
import org.bson.Document;

import java.util.Random;

public class DefeatTypeObjective extends Objective
{
    private Type type;

    public DefeatTypeObjective(int target, Type type)
    {
        super(ObjectiveType.DEFEAT_POKEMON_TYPE, target);
        this.type = type;
    }

    public DefeatTypeObjective()
    {
        this(Objective.randomTargetAmount(1, 10), Type.values()[new Random().nextInt(Type.values().length)]);
    }

    @Override
    public String getDesc()
    {
        return "Defeat any " + this.target + " Pokemon that are " + Global.normalCase(this.type.toString()) + " Type";
    }

    @Override
    public Document addObjectiveData(Document document)
    {
        return super.addObjectiveData(document)
                .append("type", this.type.toString());
    }

    public DefeatTypeObjective setType(String type)
    {
        this.type = Type.cast(type);
        return this;
    }
}
