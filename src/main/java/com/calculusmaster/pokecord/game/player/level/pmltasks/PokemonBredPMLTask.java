package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerData;
import com.calculusmaster.pokecord.util.enums.StatisticType;

public class PokemonBredPMLTask extends AbstractPMLTask
{
    private final int amount;

    public PokemonBredPMLTask(int amount)
    {
        super(LevelTaskType.POKEMON_BRED);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerData p)
    {
        return p.getStatistics().get(StatisticType.POKEMON_BRED) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerData p)
    {
        return p.getStatistics().get(StatisticType.POKEMON_BRED) + " / " + this.amount + " Pokemon Bred";
    }
}
