package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public class PokemonOwnedPMLTask extends AbstractPMLTask
{
    private final int amount;

    public PokemonOwnedPMLTask(int amount)
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
