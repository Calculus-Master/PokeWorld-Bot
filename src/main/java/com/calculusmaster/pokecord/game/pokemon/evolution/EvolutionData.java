package com.calculusmaster.pokecord.game.pokemon.evolution;

import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.evolution.triggers.*;

import java.util.ArrayList;
import java.util.List;

public class EvolutionData
{
    //Fields
    private final String evolutionID;
    private final PokemonEntity source;
    private final PokemonEntity target;
    private final List<EvolutionTrigger> triggers;

    //Properties – Avoiding looping through the trigger list constantly
    private boolean hasLevelTrigger = false; public boolean hasLevelTrigger() { return this.hasLevelTrigger; }
    private boolean hasTradeTrigger = false; public boolean hasTradeTrigger() { return this.hasTradeTrigger; }
    private boolean hasItemTrigger = false; public boolean hasItemTrigger() { return this.hasItemTrigger; }

    EvolutionData(PokemonEntity source, PokemonEntity target, EvolutionTrigger trigger1, EvolutionTrigger... triggers)
    {
        this.evolutionID = "EVO_DATA__" + source.toString() + "->" + target.toString();
        this.source = source;
        this.target = target;

        this.triggers = new ArrayList<>(List.of(trigger1));
        this.triggers.addAll(List.of(triggers));

        for(EvolutionTrigger t : this.triggers)
        {
            if(t instanceof LevelEvoTrigger)
            {
                this.hasLevelTrigger = true;
                break;
            }
            else if(t instanceof TradeEvoTrigger || t instanceof TradeWithEvoTrigger)
            {
                this.hasTradeTrigger = true;
                break;
            }
            else if(t instanceof ItemEvoTrigger)
            {
                this.hasItemTrigger = true;
                break;
            }
        }
    }

    public boolean validate(Pokemon p, String serverID)
    {
        return this.triggers.stream().allMatch(t -> t.canEvolve(p, serverID));
    }

    public String getEvolutionID()
    {
        return this.evolutionID;
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
