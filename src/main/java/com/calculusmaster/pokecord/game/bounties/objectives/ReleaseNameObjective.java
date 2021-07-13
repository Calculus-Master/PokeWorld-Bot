package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.util.PokemonRarity;
import org.bson.Document;

public class ReleaseNameObjective extends Objective
{
    private String name;

    public ReleaseNameObjective()
    {
        super(ObjectiveType.RELEASE_POKEMON_NAME, Objective.randomTargetAmount(1, 5));
        this.name = PokemonRarity.getSpawn();
    }

    @Override
    public String getDesc()
    {
        return "Release " + this.target + " " + this.name;
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

    public ReleaseNameObjective setName(String name)
    {
        this.name = name;
        return this;
    }
}
