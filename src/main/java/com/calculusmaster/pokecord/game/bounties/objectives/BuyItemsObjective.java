package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;

public class BuyItemsObjective extends Objective
{
    public BuyItemsObjective()
    {
        super(ObjectiveType.BUY_ITEMS, Objective.randomTargetAmount(1, 5));
    }

    @Override
    public String getDesc()
    {
        return "Buy " + this.target + " " + (this.target > 1 ? "items" : "item") + " from the shop";
    }
}
