package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;

public class PokemonDynamaxedPMLTask extends AbstractPMLTask
{
    private final int amount;

    public PokemonDynamaxedPMLTask(int amount)
    {
        super(LevelTaskType.POKEMON_DYNAMAXED);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getStatistics().get(PlayerStatistic.DYNAMAXED_POKEMON) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getStatistics().get(PlayerStatistic.DYNAMAXED_POKEMON) + " / " + this.amount + " Pokemon Dynamaxed";
    }
}