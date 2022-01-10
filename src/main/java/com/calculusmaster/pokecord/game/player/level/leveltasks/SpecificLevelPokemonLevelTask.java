package com.calculusmaster.pokecord.game.player.level.leveltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public class SpecificLevelPokemonLevelTask extends AbstractLevelTask
{
    private final int amount;
    private final int level;

    public SpecificLevelPokemonLevelTask(int amount, int level)
    {
        super(LevelTaskType.POKEMON_LEVEL);
        this.amount = amount;
        this.level = level;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getPokemon().stream().filter(pokemon -> pokemon.getLevel() >= this.level).count() >= amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getPokemon().stream().filter(pokemon -> pokemon.getLevel() >= this.level).count() + " / " + this.amount + " Level %s Pokemon".formatted(this.level);
    }
}
