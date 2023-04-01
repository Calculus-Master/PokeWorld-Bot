package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractPoolObjective;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;

import java.util.Random;
import java.util.stream.Collectors;

public class DefeatPoolObjective extends AbstractPoolObjective
{
    public DefeatPoolObjective()
    {
        super(ObjectiveType.DEFEAT_POKEMON_POOL);
    }

    @Override
    protected void setRandomPool()
    {
        int size = new Random().nextInt(16) + 5;
        for(int i = 0; i < size; i++) this.pool.add(PokemonRarity.getSpawn().toString());
    }

    @Override
    public String getDesc()
    {
        return "Defeat " + this.target + " Pokemon from this list: " + this.pool.stream().map(e -> PokemonEntity.cast(e).getName()).collect(Collectors.joining(", "));
    }
}
