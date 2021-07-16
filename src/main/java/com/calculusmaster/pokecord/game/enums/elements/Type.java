package com.calculusmaster.pokecord.game.enums.elements;

import com.calculusmaster.pokecord.util.Global;

import java.awt.*;

public enum Type
{
    NORMAL(170, 170, 153),
    FIRE(255, 68, 34),
    WATER(51, 153, 255),
    ELECTRIC(255, 204, 51),
    GRASS(119, 204, 85),
    ICE(102, 204, 255),
    FIGHTING(187, 85, 68),
    POISON(170, 85, 153),
    GROUND(221, 187, 85),
    FLYING(136, 153, 255),
    PSYCHIC(255, 85, 153),
    BUG(170, 187, 34),
    ROCK(187, 170, 102),
    GHOST(102, 102, 187),
    DRAGON(119, 102, 238),
    DARK(119, 85, 68),
    STEEL(170, 170, 187),
    FAIRY(238, 153, 238);

    private Color color;
    Type(int r, int g, int b)
    {
        this.color = new Color(r, g, b);
    }
    public Color getColor()
    {
        return this.color;
    }

    public String getStyledName()
    {
        return Global.normalCase(this.toString());
    }

    public static Type cast(String type)
    {
        return (Type) Global.getEnumFromString(values(), type);
    }
}
