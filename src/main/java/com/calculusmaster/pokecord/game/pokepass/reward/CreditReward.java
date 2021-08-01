package com.calculusmaster.pokecord.game.pokepass.reward;

import com.calculusmaster.pokecord.game.pokepass.TierReward;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public class CreditReward extends TierReward
{
    private int amount;

    public CreditReward(int amount)
    {
        this.amount = amount;
    }

    @Override
    public String grantReward(PlayerDataQuery player)
    {
        player.changeCredits(this.amount);

        return "You earned " + this.amount + " credits!";
    }

    @Override
    public String getTierDescription()
    {
        return "Credit Reward: **" + this.amount + "** credits!";
    }
}
