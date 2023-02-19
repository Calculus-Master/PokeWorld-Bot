package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractPoolObjective;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;

import java.util.Random;

public class CatchPoolObjective extends AbstractPoolObjective
{
    public CatchPoolObjective()
    {
        super(ObjectiveType.CATCH_POKEMON_POOL);
    }

    @Override
    protected void setRandomPool()
    {
        int size = new Random().nextInt(20) + 5;
        for(int i = 0; i < size; i++) this.pool.add(PokemonRarity.getSpawn());
    }

    @Override
    public String getDesc()
    {
        return "Catch " + this.target + " Pokemon from this list: " + this.pool;
    }
}
