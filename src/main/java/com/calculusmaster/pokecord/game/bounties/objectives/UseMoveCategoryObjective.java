package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractCategoryObjective;
import com.calculusmaster.pokecord.util.Global;

public class UseMoveCategoryObjective extends AbstractCategoryObjective
{
    public UseMoveCategoryObjective()
    {
        super(ObjectiveType.USE_MOVES_CATEGORY);
    }

    @Override
    public String getDesc()
    {
        return "Use " + this.target + " " + Global.normalCase(this.category.toString()) + " Moves";
    }
}
