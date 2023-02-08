package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public class SpecificLevelPokemonPMLTask extends AbstractPMLTask
{
    private final int amount;
    private final int level;

    public SpecificLevelPokemonPMLTask(int amount, int level)
    {
        super(LevelTaskType.POKEMON_LEVEL);
        this.amount = amount;
        this.level = level;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getPokemon().stream().filter(pokemon -> pokemon.getLevel() >= this.level).count() >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getPokemon().stream().filter(pokemon -> pokemon.getLevel() >= this.level).count() + " / " + this.amount + " Level %s Pokemon".formatted(this.level);
    }
}
