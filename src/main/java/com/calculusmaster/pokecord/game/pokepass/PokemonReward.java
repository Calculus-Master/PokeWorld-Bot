package com.calculusmaster.pokecord.game.pokepass;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public class PokemonReward extends TierReward
{
    public PokemonReward()
    {

    }

    @Override
    public String grantReward(PlayerDataQuery player)
    {
        return "";
    }
}
