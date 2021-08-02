package com.calculusmaster.pokecord.game.player.pokepass;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public abstract class TierReward
{
    public abstract String grantReward(PlayerDataQuery player);

    public abstract String getTierDescription();
}
