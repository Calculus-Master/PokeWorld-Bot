package com.calculusmaster.pokecord.game.duel.restrictions.types;

import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestriction;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.List;

public class EvenOddDexNumberRestriction extends TeamRestriction
{
    private final boolean even;

    public EvenOddDexNumberRestriction(boolean even)
    {
        super("EVEN_ODD_DEX_NUMBER" + (even ? "_EVEN" : "_ODD"));
        this.even = even;
    }

    @Override
    public boolean validate(List<Pokemon> team)
    {
        return team.stream().allMatch(p -> p.getData().dex % 2 == (this.even ? 0 : 1));
    }

    @Override
    public String getDescription()
    {
        return "Team must contain only Pokemon with an **" + (this.even ? "even" : "odd") + " PokeDex number**.";
    }
}
