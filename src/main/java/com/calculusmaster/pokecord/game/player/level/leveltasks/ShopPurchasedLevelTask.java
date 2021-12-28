package com.calculusmaster.pokecord.game.player.level.leveltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;

public class ShopPurchasedLevelTask extends AbstractLevelTask
{
    private final int amount;

    public ShopPurchasedLevelTask(int amount)
    {
        super(LevelTaskType.SHOP_PURCHASED);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getStats().get(PlayerStatistic.SHOP_ITEMS_BOUGHT) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getStats().get(PlayerStatistic.SHOP_ITEMS_BOUGHT) + " / " + this.amount + " Items";
    }
}
