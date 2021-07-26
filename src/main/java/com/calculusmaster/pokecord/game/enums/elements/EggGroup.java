package com.calculusmaster.pokecord.game.enums.elements;

public enum EggGroup
{
    MONSTER("Monster"),
    WATER_1("Water 1"),
    BUG("Bug"),
    FLYING("Flying"),
    FIELD("Field/Ground"),
    FAIRY("Fairy"),
    GRASS("Grass/Plant"),
    HUMAN_LIKE("Human-Like"),
    WATER_3("Water 3"),
    MINERAL("Mineral"),
    AMORPHOUS("Indeterminate"),
    WATER_2("Water 2"),
    DITTO("Ditto"),
    DRAGON("Dragon"),
    NO_EGGS("Undiscovered");

    private String name;
    EggGroup(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }
}
