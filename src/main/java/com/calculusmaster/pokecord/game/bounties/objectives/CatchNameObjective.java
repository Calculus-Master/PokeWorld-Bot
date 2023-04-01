package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractNameObjective;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;

public class CatchNameObjective extends AbstractNameObjective
{
    public CatchNameObjective()
    {
        super(ObjectiveType.CATCH_POKEMON_NAME);
    }

    @Override
    protected void setRandomName()
    {
        this.entityName = PokemonRarity.getSpawn().toString();
    }

    @Override
    public String getDesc()
    {
        return "Catch " + this.target + " " + PokemonEntity.cast(this.entityName).getName();
    }
}
