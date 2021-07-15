package com.calculusmaster.pokecord.game.bounties.objectives.core;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.enums.elements.Category;
import org.bson.Document;

import java.util.Random;

public abstract class AbstractCategoryObjective extends Objective
{
    protected Category category;

    public AbstractCategoryObjective(ObjectiveType type)
    {
        super(type);
        this.category = Category.values()[new Random().nextInt(Category.values().length)];
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

    public void setCategory(String category)
    {
        this.category = Category.cast(category);
    }
}
