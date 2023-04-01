package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractPoolObjective;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;

import java.util.Random;
import java.util.stream.Collectors;

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
        for(int i = 0; i < size; i++) this.pool.add(PokemonRarity.getSpawn().toString());
    }

    @Override
    public String getDesc()
    {
        return "Release " + this.target + " Pokemon from this list: " + this.pool.stream().map(e -> PokemonEntity.cast(e).getName()).collect(Collectors.joining(", "));
    }
}
