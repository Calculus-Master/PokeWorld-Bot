package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractPoolObjective;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;

import java.util.Random;

public class ReleasePoolObjective extends AbstractPoolObjective
{
    public ReleasePoolObjective()
    {
        super(ObjectiveType.RELEASE_POKEMON_POOL);
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
        return "Release " + this.target + " Pokemon from this list: " + this.pool;
    }
}
