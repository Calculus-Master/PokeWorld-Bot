package com.calculusmaster.pokecord.game.enums.elements;

import java.util.Arrays;

import static com.calculusmaster.pokecord.game.enums.elements.Region.*;

public enum Location
{
    //Kanto
    //Johto
    //Sinnoh
    MT_CORONET(SINNOH),
    ROUTE_217_SINNOH(SINNOH),
    ETERNA_FOREST(SINNOH),
    //Hoenn
    NEW_MAUVILLE(HOENN),
    SHOAL_CAVE(HOENN),
    PETALBURG_WOODS(HOENN),
    //Unova
    CHARGESTONE_CAVE(UNOVA),
    TWIST_MOUNTAIN(UNOVA),
    PINWHEEL_FOREST(UNOVA),
    //Kalos
    ROUTE_13_KALOS(KALOS),
    FROST_CAVERN(KALOS),
    ROUTE_20_KALOS(KALOS),
    //Alola
    VAST_PONI_CANYON(ALOLA),
    BLUSH_MOUNTAIN(ALOLA),
    MOUNT_LANAKILA(ALOLA),
    LUSH_JUNGLE(ALOLA),
    //Galar
    TOWER_OF_WATER(GALAR),
    TOWER_OF_DARKNESS(GALAR);

    public Region region;

    Location(Region region)
    {
        this.region = region;
    }

    private boolean is(Location... locations)
    {
        return Arrays.asList(locations).contains(this);
    }

    public boolean isMagneticField()
    {
        return this.is(MT_CORONET, CHARGESTONE_CAVE, ROUTE_13_KALOS, NEW_MAUVILLE, VAST_PONI_CANYON, BLUSH_MOUNTAIN);
    }

    public boolean isIcyRock()
    {
        return this.is(ROUTE_217_SINNOH, TWIST_MOUNTAIN, FROST_CAVERN, SHOAL_CAVE, MOUNT_LANAKILA);
    }

    public boolean isMossyRock()
    {
        return this.is(ETERNA_FOREST, PINWHEEL_FOREST, ROUTE_20_KALOS, PETALBURG_WOODS, LUSH_JUNGLE);
    }

    public static Location cast(String s)
    {
        for(Location l : values()) if(s.equalsIgnoreCase(l.toString())) return l;
        return null;
    }
}
