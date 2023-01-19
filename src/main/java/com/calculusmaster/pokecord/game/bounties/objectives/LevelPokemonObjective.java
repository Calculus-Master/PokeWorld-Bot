package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class LevelPokemonObjective extends Objective
{
    public LevelPokemonObjective()
    {
        super(ObjectiveType.LEVEL_POKEMON);
    }

    @Override
    public String getDesc()
    {
        return "Level Up " + " Pokemon " + this.target + " times";
    }
}
