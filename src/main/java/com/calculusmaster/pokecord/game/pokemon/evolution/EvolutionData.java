package com.calculusmaster.pokecord.game.pokemon.evolution;

import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.evolution.triggers.EvolutionTrigger;
import com.calculusmaster.pokecord.game.pokemon.evolution.triggers.LevelEvoTrigger;

import java.util.ArrayList;
import java.util.List;

public class EvolutionData
{
    //Fields
    private final PokemonEntity source;
    private final PokemonEntity target;
    private final List<EvolutionTrigger> triggers;

    //Properties – Avoiding looping through the trigger list constantly
    private boolean hasLevelTrigger = false; public boolean hasLevel() { return this.hasLevelTrigger; }

    EvolutionData(PokemonEntity source, PokemonEntity target, EvolutionTrigger trigger1, EvolutionTrigger... triggers)
    {
        this.source = source;
        this.target = target;

        this.triggers = new ArrayList<>(List.of(trigger1));
        this.triggers.addAll(List.of(triggers));

        for(EvolutionTrigger t : this.triggers)
        {
            if(t instanceof LevelEvoTrigger) this.hasLevelTrigger = true;
        }
    }

    public PokemonEntity getSource()
    {
        return this.source;
    }

    public PokemonEntity getTarget()
    {
        return this.target;
    }

    public List<EvolutionTrigger> getTriggers()
    {
        return this.triggers;
    }
}
