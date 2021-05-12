package com.calculusmaster.pokecord.game.enums.elements;

import com.calculusmaster.pokecord.util.Global;

public enum Category
{
    PHYSICAL,
    SPECIAL,
    STATUS;

    public static Category cast(String category)
    {
        return (Category) Global.getEnumFromString(values(), category);
    }
}
