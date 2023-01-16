package com.calculusmaster.pokecord.game.duel.teamrules;

import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;

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
        int countLegendary = check.apply(PokemonRarity.LEGENDARY);
        int countMythical = check.apply(PokemonRarity.MYTHICAL);
        int countUltraBeast = check.apply(PokemonRarity.ULTRA_BEAST);
        int countMega = check.apply(PokemonRarity.MEGA);

        if(team.size() <= 3) return countLegendary + countMythical + countUltraBeast + countMega <= 1;
        else if(countLegendary > 1) return false;
        else if(countLegendary > 0) return countMythical + countUltraBeast <= 1 && countMega <= 1;
        else return countMythical + countUltraBeast <= 2 && countMega <= 1;
    }

    @Override
    public String getDescription()
    {
        return "Team must follow **standard Duel team restrictions**. A maximum of *1 Legendary Pokemon* is allowed, and for teams containing a Legendary Pokemon, up to *1 Mythical/Ultra Beast* and *1 Mega-Evolved Pokemon* are allowed. For teams containing *no Legendary Pokemon*, up to *2 Mythical/Ultra Beast* and *1 Mega-Evolved Pokemon* are allowed. For teams with *3 or fewer Pokemon*, only *1 Legendary, Mythical, Ultra Beast, or Mega-Evolved Pokemon* is allowed.";
    }
}
