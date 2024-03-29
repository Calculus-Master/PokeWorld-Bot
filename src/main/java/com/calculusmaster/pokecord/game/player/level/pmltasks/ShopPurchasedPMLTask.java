package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerData;
import com.calculusmaster.pokecord.util.enums.StatisticType;

public class ShopPurchasedPMLTask extends AbstractPMLTask
{
    private final int amount;

    public ShopPurchasedPMLTask(int amount)
    {
        super(LevelTaskType.SHOP_PURCHASED);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerData p)
    {
        return p.getStatistics().get(StatisticType.ITEMS_BOUGHT) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerData p)
    {
        return p.getStatistics().get(StatisticType.ITEMS_BOUGHT) + " / " + this.amount + " Items";
    }
}
