package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.util.Global;
import org.bson.Document;

import java.util.Random;

public class UseMoveCategoryObjective extends Objective
{
    private Category category;

    public UseMoveCategoryObjective()
    {
        super(ObjectiveType.USE_MOVES_CATEGORY, Objective.randomTargetAmount(10, 30));
        this.category = Category.values()[new Random().nextInt(Category.values().length)];
    }

    @Override
    public String getDesc()
    {
        return "Use " + this.target + " " + Global.normalCase(this.category.toString()) + " Moves";
    }

    @Override
    public Document addObjectiveData(Document document)
    {
        return super.addObjectiveData(document)
                .append("category", this.category.toString());
    }

    public Category getCategory()
    {
        return this.category;
    }

    public UseMoveCategoryObjective setCategory(String category)
    {
        this.category = Category.cast(category);
        return this;
    }
}
