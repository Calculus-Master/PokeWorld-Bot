package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.StatisticType;

public class PokemonEvolvedPMLTask extends AbstractPMLTask
{
    private final int amount;

    public PokemonEvolvedPMLTask(int amount)
    {
        super(LevelTaskType.POKEMON_EVOLVED);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getStatistics().get(StatisticType.POKEMON_EVOLVED) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getStatistics().get(StatisticType.POKEMON_EVOLVED) + " / " + this.amount + " Pokemon Evolved";
    }
}
