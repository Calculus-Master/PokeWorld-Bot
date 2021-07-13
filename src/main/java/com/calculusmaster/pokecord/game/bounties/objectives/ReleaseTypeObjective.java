package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.util.Global;
import org.bson.Document;

import java.util.Random;

public class ReleaseTypeObjective extends Objective
{
    private Type type;

    public ReleaseTypeObjective()
    {
        super(ObjectiveType.RELEASE_POKEMON_TYPE, Objective.randomTargetAmount(5, 15));
        this.type = Type.values()[new Random().nextInt(Type.values().length)];
    }

    @Override
    public String getDesc()
    {
        return "Release " + this.target + " Pokemon that are " + Global.normalCase(this.type.toString()) + " Type";
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

    public ReleaseTypeObjective setType(String type)
    {
        this.type = Type.cast(type);
        return this;
    }
}
