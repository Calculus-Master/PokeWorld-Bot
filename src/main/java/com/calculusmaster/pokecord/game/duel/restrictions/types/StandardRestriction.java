package com.calculusmaster.pokecord.game.duel.restrictions.types;

import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestriction;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.evolution.MegaEvolutionRegistry;

import java.util.List;

public class StandardRestriction extends TeamRestriction
{
    public StandardRestriction()
    {
        super("STANDARD_RESTRICTION");
    }

    @Override
    public boolean validate(List<Pokemon> team)
    {
        int countMegaPrimalLegendary = 0, countLegendary = 0, countMythical = 0, countUltraBeast = 0, countMega = 0;

        for(Pokemon p : team)
        {
            if(MegaEvolutionRegistry.isMegaLegendary(p.getEntity())) countMegaPrimalLegendary++;
            else if(PokemonRarity.isLegendary(p.getEntity())) countLegendary++;
            else if(PokemonRarity.isMythical(p.getEntity())) countMythical++;
            else if(PokemonRarity.isUltraBeast(p.getEntity())) countUltraBeast++;
            else if(MegaEvolutionRegistry.isMega(p.getEntity())) countMega++;
        }

        //PLP: Pokemon Load Points
        int totalPLP = countMegaPrimalLegendary * 4 + countLegendary * 3 + countMythical * 2 + countUltraBeast + countMega;

        return team.size() >= 3 && totalPLP <= 4;
    }

    @Override
    public String getDescription()
    {
        return """
                Team must follow **standard Duel team restrictions.** The team must contain **at least 3 Pokemon**, and **cannot exceed 4 PLPs**.
                PLP costs are as follows:
                *Mega-Evolved Legendary*: `4` PLPs
                *Legendary*: `3` PLPs
                *Mythical*: `2` PLPs
                *Ultra Beast*: `1` PLPs
                *Mega-Evolved*: `1` PLPs
                *Other*: `0` PLPs
                """;
    }
}
