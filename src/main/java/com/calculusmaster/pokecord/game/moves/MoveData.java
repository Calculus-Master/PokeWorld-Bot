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

    public final boolean isZMove;
    public final boolean isMaxMove;

    public MoveData(String name, Type type, Category category, int basePower, int baseAccuracy, List<String> flavor, boolean isZMove, boolean isMaxMove)
    {
        this.name = name;
        this.type = type;
        this.category = category;
        this.basePower = basePower;
        this.baseAccuracy = baseAccuracy;
        this.flavor = new ArrayList<>(List.copyOf(flavor));

        this.isZMove = false;
        this.isMaxMove = false;
    }

    public MoveData copy()
    {
        return new MoveData(this.name, this.type, this.category, this.basePower, this.baseAccuracy, this.flavor, this.isZMove, this.isMaxMove);
    }

    @Override
    public String toString()
    {
        return "MoveData{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", category=" + category +
                ", basePower=" + basePower +
                ", baseAccuracy=" + baseAccuracy +
                ", flavor=" + flavor +
                ", isZMove=" + isZMove +
                ", isMaxMove=" + isMaxMove +
                '}';
    }
}
