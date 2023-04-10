package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.StatisticType;

public class PokemonCaughtPMLTask extends AbstractPMLTask
{
    private final int amount;

    public PokemonCaughtPMLTask(int amount)
    {
        super(LevelTaskType.POKEMON_CAUGHT);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getStatistics().get(StatisticType.POKEMON_CAUGHT) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getStatistics().get(StatisticType.POKEMON_CAUGHT) + " / " + this.amount + " Pokemon Caught";
    }
}
