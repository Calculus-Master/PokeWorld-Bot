package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;

public class PokemonBredPMLTask extends AbstractPMLTask
{
    private final int amount;

    public PokemonBredPMLTask(int amount)
    {
        super(LevelTaskType.POKEMON_BRED);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getStatistics().get(PlayerStatistic.POKEMON_BRED) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getStatistics().get(PlayerStatistic.POKEMON_BRED) + " / " + this.amount + " Pokemon Bred";
    }
}
