package com.calculusmaster.pokecord.game.pokemon.sort;

import java.util.Arrays;
import java.util.List;

public enum MarketSorterFlag
{
    LISTINGS,
    BOT,
    PRICE;

    public List<String> flags;
    MarketSorterFlag(String... flags)
    {
        this.flags = Arrays.asList(flags);
    }
}
