package com.calculusmaster.pokecord.game.player.pokepass.reward;

import com.calculusmaster.pokecord.game.player.pokepass.TierReward;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public class EmptyReward extends TierReward
{
    @Override
    public String grantReward(PlayerDataQuery player)
    {
        return "";
    }

    @Override
    public String getTierDescription()
    {
        return "No Reward!";
    }
}