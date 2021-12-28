package com.calculusmaster.pokecord.game.player.level.leveltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;

public class PokemonEvolvedLevelTask extends AbstractLevelTask
{
    private final int amount;

    public PokemonEvolvedLevelTask(int amount)
    {
        super(LevelTaskType.POKEMON_EVOLVED);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getStats().get(PlayerStatistic.POKEMON_EVOLVED) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getStats().get(PlayerStatistic.POKEMON_EVOLVED) + " / " + this.amount + " Pokemon Evolved";
    }
}
