package com.calculusmaster.pokecord.game.enums.elements;

import java.util.List;

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
    BOUND("BND"),
    BADLY_POISONED("BPSN"),
    INFATUATED("INF"),
    SEEDED("SEED");

    private final String abbrev;
    StatusCondition(String abbrev)
    {
        this.abbrev = abbrev;
    }

    public String getAbbrev()
    {
        return this.abbrev;
    }

    public boolean isNonVolatile()
    {
        return List.of(BURNED, ASLEEP, BADLY_POISONED, FROZEN, PARALYZED, POISONED).contains(this);
    }
}
