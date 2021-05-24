package com.calculusmaster.pokecord.game.enums.items;

import com.calculusmaster.pokecord.game.enums.elements.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ZCrystal
{
    BUGINIUM_Z,
    DARKINIUM_Z,
    DRAGONIUM_Z,
    ELECTRIUM_Z,
    FAIRIUM_Z,
    FIGHTINIUM_Z,
    FIRIUM_Z,
    FLYINIUM_Z,
    GHOSTIUM_Z,
    GRASSIUM_Z,
    GROUNDIUM_Z,
    ICIUM_Z,
    NORMALIUM_Z,
    POISONIUM_Z,
    PSYCHIUM_Z,
    ROCKIUM_Z,
    STEELIUM_Z,
    WATERIUM_Z,
    ALORAICHIUM_Z,
    DECIDIUM_Z,
    EEVIUM_Z,
    INCINIUM_Z,
    KOMMOIUM_Z,
    LUNALIUM_Z,
    LYCANIUM_Z,
    MARSHADIUM_Z,
    MEWNIUM_Z,
    MIMIKIUM_Z,
    PIKANIUM_Z,
    PIKASHUNIUM_Z,
    PRIMARIUM_Z,
    SNORLIUM_Z,
    SOLGANIUM_Z,
    TAPUNIUM_Z,
    ULTRANECROZIUM_Z;

    public String getStyledName()
    {
        return this.toString().charAt(0) + this.toString().substring(0, this.toString().indexOf("_")).toLowerCase() + " Z";
    }

    public static ZCrystal cast(String z)
    {
        for(ZCrystal zc : values()) if(z.equals(zc.getStyledName())) return zc;
        return null;
    }

    public static ZCrystal getCrystalOfType(Type t)
    {
        return switch(t) {
            case BUG -> ZCrystal.BUGINIUM_Z;
            case DARK -> ZCrystal.DARKINIUM_Z;
            case DRAGON -> ZCrystal.DRAGONIUM_Z;
            case ELECTRIC -> ZCrystal.ELECTRIUM_Z;
            case FAIRY -> ZCrystal.FAIRIUM_Z;
            case FIGHTING -> ZCrystal.FIGHTINIUM_Z;
            case FIRE -> ZCrystal.FIRIUM_Z;
            case FLYING -> ZCrystal.FLYINIUM_Z;
            case GHOST -> ZCrystal.GHOSTIUM_Z;
            case GRASS -> ZCrystal.GRASSIUM_Z;
            case GROUND -> ZCrystal.GROUNDIUM_Z;
            case ICE -> ZCrystal.ICIUM_Z;
            case NORMAL -> ZCrystal.NORMALIUM_Z;
            case POISON -> ZCrystal.POISONIUM_Z;
            case PSYCHIC -> ZCrystal.PSYCHIUM_Z;
            case ROCK -> ZCrystal.ROCKIUM_Z;
            case STEEL -> ZCrystal.STEELIUM_Z;
            case WATER -> ZCrystal.WATERIUM_Z;
        };
    }

    public static ZCrystal getRandomUniqueZCrystal()
    {
        List<ZCrystal> uniques = new ArrayList<>(Arrays.asList(values()).subList(18, values().length));
        Collections.shuffle(uniques);

        return uniques.get(0);
    }
}
