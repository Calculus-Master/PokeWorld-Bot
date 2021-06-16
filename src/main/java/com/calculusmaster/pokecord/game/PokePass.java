package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.game.enums.items.PokeItem;
import com.calculusmaster.pokecord.game.pokepass.*;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

import java.util.HashMap;
import java.util.Map;

public class PokePass
{
    public static Map<Integer, TierReward> passTiers = new HashMap<>();
    public static final int TIER_EXP = 15000;

    public static void init()
    {
        //TODO: Complete PokePass, Limit Certain Rewards based on Gym Level
        PokePass.addTier(0, new EmptyReward());
        PokePass.addTier(1, new RedeemReward(1));
        PokePass.addTier(2, new CreditReward(250));
        PokePass.addTier(3, new ItemReward(PokeItem.TRADE_EVOLVER));
        PokePass.addTier(4, new CreditReward(250));
        PokePass.addTier(5, new CreditReward(250));
        PokePass.addTier(6, new PokemonReward(15, 40,"Mudkip", "Oshawott", "Squirtle", "Totodile", "Popplio", "Piplup", "Froakie"));
        PokePass.addTier(7, new CreditReward(250));
        PokePass.addTier(8, new CreditReward(250));
        PokePass.addTier(9, new CreditReward(250));
        PokePass.addTier(10, new ItemReward(PokeItem.EV_REALLOCATOR));
        PokePass.addTier(11, new CreditReward(300));
        PokePass.addTier(12, new CreditReward(300));
        PokePass.addTier(13, new ItemReward(PokeItem.DRACO_PLATE));
        PokePass.addTier(14, new ItemReward(PokeItem.EV_REALLOCATOR));
        PokePass.addTier(15, new CreditReward(300));
        PokePass.addTier(16, new PokemonReward(25, 40, "Jellicent", "Gyarados"));
        PokePass.addTier(17, new CreditReward(300));
        PokePass.addTier(18, new CreditReward(300));
        PokePass.addTier(19, new CreditReward(300));
        PokePass.addTier(20, new RedeemReward(1));
    }

    public static String getTierDescription(int tier)
    {
        return passTiers.get(tier).getTierDescription();
    }

    public static boolean tierExists(int tier)
    {
        return passTiers.containsKey(tier);
    }

    public static void addTier(int tier, TierReward reward)
    {
        passTiers.put(tier, reward);
    }

    public static String reward(int tier, PlayerDataQuery player)
    {
        return player.getMention() + ": PokePass Tier Level Up! You are now Tier " + tier + "! " + passTiers.get(tier).grantReward(player);
    }
}
