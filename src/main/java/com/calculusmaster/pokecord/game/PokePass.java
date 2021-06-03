package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.game.pokepass.TierReward;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

import java.util.HashMap;
import java.util.Map;

public class PokePass
{
    public Map<Integer, TierReward> passTiers = new HashMap<>();

    public void addTier(int tier, TierReward reward)
    {
        this.passTiers.put(tier, reward);
    }

    public void reward(int tier, PlayerDataQuery player)
    {
        this.passTiers.get(tier).grantReward(player);
    }
}
