package com.calculusmaster.pokecord.game.duel.restrictions.types;

import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestriction;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;

import java.util.List;
import java.util.function.Function;

public class StandardRestriction extends TeamRestriction
{
    public StandardRestriction()
    {
        super("STANDARD_RESTRICTION");
    }

    @Override
    public boolean validate(List<Pokemon> team)
    {
        Function<List<String>, Integer> check = list -> (int)team.stream().filter(p -> list.contains(p.getName())).count();
        int countMegaPrimalLegendary = check.apply(PokemonRarity.MEGA_LEGENDARY);
        int countLegendary = check.apply(PokemonRarity.LEGENDARY);
        int countMythical = check.apply(PokemonRarity.MYTHICAL);
        int countUltraBeast = check.apply(PokemonRarity.ULTRA_BEAST);
        int countMega = check.apply(PokemonRarity.MEGA);

        //PLP: Pokemon Load Points
        int totalPLP = countMegaPrimalLegendary * 4 + countLegendary * 3 + countMythical * 2 + countUltraBeast + countMega;

        return team.size() >= 3 && totalPLP <= 4;
    }

    @Override
    public String getDescription()
    {
        return """
                Team must follow **standard Duel team restrictions.** The team must contain at least 3 Pokemon, and cannot exceed 4 PLPs. PLP costs are as follows:
                *Mega-Evolved/Primal Legendary Pokemon*:\t4 PLPs
                *Legendary Pokemon*:\t3 PLPs
                *Mythical Pokemon*:\t2 PLPs
                *Ultra Beast Pokemon*:\t1 PLPs
                *Mega-Evolved Pokemon*:\t1 PLPs
                *All Other Pokemon*:\t0 PLPs
                """;
    }
}
