package com.calculusmaster.pokecord.game.pokepass.reward;

import com.calculusmaster.pokecord.game.enums.items.PokeItem;
import com.calculusmaster.pokecord.game.pokepass.TierReward;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public class ItemReward extends TierReward
{
    private PokeItem item;

    public ItemReward(PokeItem item)
    {
        this.item = item;
    }

    @Override
    public String grantReward(PlayerDataQuery player)
    {
        player.addItem(this.item.toString());

        return "You got " + this.item.getStyledName() + "!";
    }

    @Override
    public String getTierDescription()
    {
        return "Item Reward: **" + this.item.getStyledName() + "**!";
    }
}
