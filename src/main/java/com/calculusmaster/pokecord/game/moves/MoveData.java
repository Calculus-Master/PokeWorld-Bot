package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Type;

import java.util.ArrayList;
import java.util.List;

public final class MoveData
{
    public final String name;
    public final Type type;
    public final Category category;
    public final int basePower;
    public final int baseAccuracy;
    public final List<String> flavor;

    public MoveData(String name, Type type, Category category, int basePower, int baseAccuracy, List<String> flavor)
    {
        this.name = name;
        this.type = type;
        this.category = category;
        this.basePower = basePower;
        this.baseAccuracy = baseAccuracy;
        this.flavor = new ArrayList<>(List.copyOf(flavor));
    }

    public MoveData copy()
    {
        return new MoveData(this.name, this.type, this.category, this.basePower, this.baseAccuracy, this.flavor);
    }
}
