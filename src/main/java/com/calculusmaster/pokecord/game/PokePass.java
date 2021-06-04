package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.game.pokepass.CreditReward;
import com.calculusmaster.pokecord.game.pokepass.TierReward;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

import java.util.HashMap;
import java.util.Map;

public class PokePass
{
    public Map<Integer, TierReward> passTiers = new HashMap<>();

    public static void init()
    {
        PokePass pass = new PokePass();

        pass.addTier(1, new CreditReward(500));
    }

    public void addTier(int tier, TierReward reward)
    {
        this.passTiers.put(tier, reward);
    }

    public void reward(int tier, PlayerDataQuery player)
    {
        this.passTiers.get(tier).grantReward(player);
    }
}
