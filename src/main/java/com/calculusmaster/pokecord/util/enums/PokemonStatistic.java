package com.calculusmaster.pokecord.util.enums;

public enum PokemonStatistic
{
    POKEMON_DEFEATED("count_defeated"),
    TIMES_FAINTED("count_fainted");

    public String key;
    PokemonStatistic(String key)
    {
        this.key = key;
    }
}
