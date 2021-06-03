package com.calculusmaster.pokecord.game.pokepass;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public abstract class TierReward
{
    public abstract String grantReward(PlayerDataQuery player);
}
