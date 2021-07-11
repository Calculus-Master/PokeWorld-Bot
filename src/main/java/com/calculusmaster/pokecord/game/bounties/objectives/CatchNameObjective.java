package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.util.PokemonRarity;
import org.bson.Document;

public class CatchNameObjective extends Objective
{
    private String name;

    public CatchNameObjective()
    {
        super(ObjectiveType.CATCH_POKEMON_NAME, Objective.randomTargetAmount(10, 20));
        this.name = PokemonRarity.getSpawn();
    }

    @Override
    public String getDesc()
    {
        return "Catch " + this.target + " " + this.name;
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

    public CatchNameObjective setName(String name)
    {
        this.name = name;
        return this;
    }
}
