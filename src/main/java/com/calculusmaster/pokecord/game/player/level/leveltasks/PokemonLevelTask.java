package com.calculusmaster.pokecord.game.player.level.leveltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public class PokemonLevelTask extends AbstractLevelTask
{
    private final int amount;

    public PokemonLevelTask(int amount)
    {
        super(LevelTaskType.POKEMON);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getPokemonList().size() >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getPokemonList().size() + " / " + this.amount + " Pokemon";
    }
}
