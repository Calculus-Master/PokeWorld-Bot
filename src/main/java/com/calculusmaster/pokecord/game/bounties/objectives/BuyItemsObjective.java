package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class BuyItemsObjective extends Objective
{
    public BuyItemsObjective()
    {
        super(ObjectiveType.BUY_ITEMS);
    }

    @Override
    public String getDesc()
    {
        return "Buy " + this.target + " " + (this.target > 1 ? "items" : "item") + " from the shop";
    }
}
