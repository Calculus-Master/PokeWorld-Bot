package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.StatisticType;

public class PokemonPrestigedPMLTask extends AbstractPMLTask
{
    private final int amount;

    public PokemonPrestigedPMLTask(int amount)
    {
        super(LevelTaskType.POKEMON_PRESTIGED);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getStatistics().get(StatisticType.POKEMON_PRESTIGED) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getStatistics().get(StatisticType.POKEMON_PRESTIGED) + " / " + this.amount + " Pokemon Prestiged";
    }
}
