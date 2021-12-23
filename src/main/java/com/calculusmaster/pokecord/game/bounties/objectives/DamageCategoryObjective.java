package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractCategoryObjective;
import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.util.Global;

import java.util.Random;

public class DamageCategoryObjective extends AbstractCategoryObjective
{
    public DamageCategoryObjective()
    {
        super(ObjectiveType.DAMAGE_POKEMON_CATEGORY);
        while(this.category.equals(Category.STATUS)) this.category = Category.values()[new Random().nextInt(Category.values().length)];
    }

    @Override
    public String getDesc()
    {
        return "Deal " + this.target + " damage to opponent Pokemon with " + Global.normalize(this.category.toString()) + " moves";
    }
}
