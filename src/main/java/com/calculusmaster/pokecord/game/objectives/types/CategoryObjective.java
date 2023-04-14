package com.calculusmaster.pokecord.game.objectives.types;

import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.util.Global;
import org.bson.Document;

import java.util.Random;

public class CategoryObjective extends AbstractObjective
{
    private Category category;

    @Override
    public void read(Document data)
    {
        super.read(data);
        this.category = Category.valueOf(data.getString("category"));
    }

    @Override
    public Document serialize()
    {
        return super.serialize().append("category", this.category.toString());
    }

    @Override
    public AbstractObjective generate()
    {
        this.category = Category.values()[new Random().nextInt(Category.values().length)];
        return this;
    }

    @Override
    protected String getSpecificDescription()
    {
        return "Category: " + Global.normalize(this.category.toString()) + ".";
    }

    public Category getCategory()
    {
        return this.category;
    }

    public void setCategory(Category category)
    {
        this.category = category;
    }
}
