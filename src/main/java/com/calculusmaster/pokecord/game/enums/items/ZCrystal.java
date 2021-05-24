package com.calculusmaster.pokecord.game.enums.items;

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
}
