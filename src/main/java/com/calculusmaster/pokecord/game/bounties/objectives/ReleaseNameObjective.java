package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractNameObjective;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;

public class ReleaseNameObjective extends AbstractNameObjective
{
    public ReleaseNameObjective()
    {
        super(ObjectiveType.RELEASE_POKEMON_NAME);
    }

    @Override
    protected void setRandomName()
    {
        this.name = PokemonRarity.getSpawn();
    }

    @Override
    public String getDesc()
    {
        return "Release " + this.target + " " + this.name;
    }
}
