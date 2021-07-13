package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.util.PokemonRarity;
import org.bson.Document;

import java.util.List;
import java.util.Random;

public class DefeatPokemonPoolObjective extends Objective
{
    private List<String> pool;

    public DefeatPokemonPoolObjective()
    {
        super(ObjectiveType.DEFEAT_POKEMON_POOL, Objective.randomTargetAmount(5, 15));

        int size = new Random().nextInt(16) + 5;
        for(int i = 0; i < size; i++) this.pool.add(PokemonRarity.getSpawn());
    }

    @Override
    public String getDesc()
    {
        return "Defeat " + this.target + " Pokemon from this list: " + this.pool;
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

    public DefeatPokemonPoolObjective setPool(List<String> pool)
    {
        this.pool = pool;
        return this;
    }
}
