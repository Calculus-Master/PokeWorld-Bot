package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.game.enums.items.PokeItem;
import com.calculusmaster.pokecord.game.pokepass.*;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

import java.util.HashMap;
import java.util.Map;

public class PokePass
{
    public Map<Integer, TierReward> passTiers = new HashMap<>();
    public static final int TIER_EXP = 50000;

    public static void init()
    {
        PokePass pass = new PokePass();

        //TODO: Complete PokePass, Limit Certain Rewards based on Gym Level
        pass.addTier(0, new EmptyReward());
        pass.addTier(1, new CreditReward(250));
        pass.addTier(2, new CreditReward(250));
        pass.addTier(3, new ItemReward(PokeItem.TRADE_EVOLVER));
        pass.addTier(4, new CreditReward(250));
        pass.addTier(5, new CreditReward(250));
        pass.addTier(6, new PokemonReward(15, "Mudkip", "Oshawott", "Squirtle", "Totodile", "Popplio", "Piplup", "Froakie"));
        pass.addTier(7, new CreditReward(250));
        pass.addTier(8, new CreditReward(250));
        pass.addTier(9, new CreditReward(250));
        pass.addTier(10, new ItemReward(PokeItem.EV_REALLOCATOR));
    }

    public static String getTierDescription(int tier)
    {
        return new PokePass().passTiers.get(tier).getTierDescription();
    }

    public static boolean tierExists(int tier)
    {
        return new PokePass().passTiers.containsKey(tier);
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
