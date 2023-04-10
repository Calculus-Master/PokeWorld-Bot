package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.StatisticType;

public class PokemonEggsHatchedPMLTask extends AbstractPMLTask
{
    private final int amount;

    public PokemonEggsHatchedPMLTask(int amount)
    {
        super(LevelTaskType.EGGS_HATCHED);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getStatistics().get(StatisticType.EGGS_HATCHED) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getStatistics().get(StatisticType.EGGS_HATCHED) + " / " + this.amount + " Pokemon Eggs Hatched";
    }
}
