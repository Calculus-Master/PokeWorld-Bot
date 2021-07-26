package com.calculusmaster.pokecord.game.pokepass;

import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.pokemon.PokemonSkin;
import com.calculusmaster.pokecord.game.pokepass.reward.*;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

import java.util.HashMap;
import java.util.Map;

public class PokePass
{
    public static Map<Integer, TierReward> passTiers = new HashMap<>();
    public static final int TIER_EXP = 15000;

    public static void init()
    {
        PokePass.addTier(0, new EmptyReward());
        PokePass.addTier(1, new RedeemReward(1));
        PokePass.addTier(2, new CreditReward(250));
        PokePass.addTier(3, new ItemReward(Item.TRADE_EVOLVER));
        PokePass.addTier(4, new CreditReward(250));
        PokePass.addTier(5, new CreditReward(250));
        PokePass.addTier(6, new PokemonReward(15, 40,"Mudkip", "Oshawott", "Squirtle", "Totodile", "Popplio", "Piplup", "Froakie"));
        PokePass.addTier(7, new CreditReward(250));
        PokePass.addTier(8, new ItemReward(Item.EV_CLEARER));
        PokePass.addTier(9, new CreditReward(250));
        PokePass.addTier(10, new ItemReward(Item.EV_REALLOCATOR));
        PokePass.addTier(11, new CreditReward(300));
        PokePass.addTier(12, new CreditReward(300));
        PokePass.addTier(13, new CreditReward(300));
        PokePass.addTier(14, new ItemReward(Item.EV_REALLOCATOR));
        PokePass.addTier(15, new CreditReward(300));
        PokePass.addTier(16, new PokemonReward(25, 40, "Jellicent", "Gyarados"));
        PokePass.addTier(17, new CreditReward(300));
        PokePass.addTier(18, new ItemReward(Item.EV_CLEARER));
        PokePass.addTier(19, new ItemReward(Item.EV_REALLOCATOR));
        PokePass.addTier(20, new RedeemReward(1));
        PokePass.addTier(18, new CreditReward(350));
        PokePass.addTier(19, new CreditReward(350));
        PokePass.addTier(20, new CreditReward(350));
        PokePass.addTier(21, new ItemReward(Item.EV_REALLOCATOR));
        PokePass.addTier(22, new CreditReward(350));
        PokePass.addTier(23, new CreditReward(350));
        PokePass.addTier(24, new CreditReward(350));
        PokePass.addTier(25, new SkinReward(PokemonSkin.ULTRA_NECROZMA_GREEN));
        PokePass.addTier(26, new ItemReward(Item.EV_REALLOCATOR));
        PokePass.addTier(27, new CreditReward(375));
        PokePass.addTier(28, new CreditReward(375));
        PokePass.addTier(29, new CreditReward(375));
        PokePass.addTier(30, new RedeemReward(1));
        PokePass.addTier(31, new ItemReward(Item.EV_REALLOCATOR));
        PokePass.addTier(32, new CreditReward(400));
        PokePass.addTier(33, new CreditReward(400));
        PokePass.addTier(34, new CreditReward(400));
        PokePass.addTier(35, new CreditReward(400));
        PokePass.addTier(36, new CreditReward(400));
        PokePass.addTier(37, new ItemReward(Item.EV_REALLOCATOR));
        PokePass.addTier(38, new CreditReward(425));
        PokePass.addTier(39, new CreditReward(425));
        PokePass.addTier(40, new ItemReward(Item.IV_REROLLER));
        PokePass.addTier(41, new RedeemReward(1));
        PokePass.addTier(42, new CreditReward(500));
        PokePass.addTier(43, new CreditReward(500));
        PokePass.addTier(44, new CreditReward(500));
        PokePass.addTier(45, new PokemonReward(40, 60, "Swampert", "Samurott", "Blastoise", "Feraligatr", "Primarina", "Empoleon", "Greninja"));
        PokePass.addTier(46, new ItemReward(Item.EV_REALLOCATOR));
        PokePass.addTier(47, new CreditReward(700));
        PokePass.addTier(48, new CreditReward(800));
        PokePass.addTier(49, new RedeemReward(2));
        PokePass.addTier(50, new CreditReward(1000));
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
