package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerData;
import com.calculusmaster.pokecord.util.enums.StatisticType;

public class PokemonDynamaxedPMLTask extends AbstractPMLTask
{
    private final int amount;

    public PokemonDynamaxedPMLTask(int amount)
    {
        super(LevelTaskType.POKEMON_DYNAMAXED);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerData p)
    {
        return p.getStatistics().get(StatisticType.POKEMON_DYNAMAXED) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerData p)
    {
        return p.getStatistics().get(StatisticType.POKEMON_DYNAMAXED) + " / " + this.amount + " Pokemon Dynamaxed";
    }
}
