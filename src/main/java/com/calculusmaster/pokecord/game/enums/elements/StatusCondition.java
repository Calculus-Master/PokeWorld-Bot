package com.calculusmaster.pokecord.game.enums.elements;

public enum StatusCondition
{
    BURNED("BRN"),
    FROZEN("FRZ"),
    PARALYZED("PAR"),
    POISONED("PSN"),
    ASLEEP("SLP"),
    CONFUSED("CNF"),
    FLINCHED("FLN"),
    CURSED("CRS"),
    NIGHTMARE("NTM"),
    BOUND("BND");

    private final String abbrev;
    StatusCondition(String abbrev)
    {
        this.abbrev = abbrev;
    }

    public String getAbbrev()
    {
        return this.abbrev;
    }
}
