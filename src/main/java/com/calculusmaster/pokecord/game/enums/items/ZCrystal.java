package com.calculusmaster.pokecord.game.enums.items;

import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Type;

import java.util.*;

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
    ULTRANECROZIUM_Z,
    //Custom Z-Crystals
    RESHIRIUM_Z,
    ZEKRIUM_Z,
    KYURIUM_Z,
    XERNIUM_Z,
    YVELTIUM_Z,
    DIANCIUM_Z,
    ARCEIUM_Z,
    RAYQUAZIUM_Z,
    ZYGARDIUM_Z,
    VOLCANIUM_Z,
    KYOGRIUM_Z,
    GROUDONIUM_Z,
    GENESECTIUM_Z,
    MELMETALIUM_Z,
    DIALGIUM_Z,
    PALKIUM_Z,
    GIRATINIUM_Z,
    ETERNIUM_Z,
    DARKRAIUM_Z;

    public String getStyledName()
    {
        return this.toString().charAt(0) + this.toString().substring(1, this.toString().indexOf("_")).toLowerCase() + " Z";
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
    
    public static boolean isValid(ZCrystal z, Move move, String pokemonName)
    {
        Map<ZCrystal, List<String>> statusOverride = new HashMap<>();
        statusOverride.put(ZCrystal.XERNIUM_Z, List.of("Geomancy"));
        statusOverride.put(ZCrystal.DARKRAIUM_Z, List.of("Dark Void"));
        statusOverride.put(ZCrystal.MELMETALIUM_Z, List.of("Acid Armor"));

        if(move.getCategory().equals(Category.STATUS))
        {
            if(!statusOverride.containsKey(z) || !statusOverride.get(z).contains(move.getName()))
                return false;
        }

        return switch(z) {
            //Type-based
            case BUGINIUM_Z -> move.getType().equals(Type.BUG);
            case DARKINIUM_Z -> move.getType().equals(Type.DARK);
            case DRAGONIUM_Z -> move.getType().equals(Type.DRAGON);
            case ELECTRIUM_Z -> move.getType().equals(Type.ELECTRIC);
            case FAIRIUM_Z -> move.getType().equals(Type.FAIRY);
            case FIGHTINIUM_Z -> move.getType().equals(Type.FIGHTING);
            case FIRIUM_Z -> move.getType().equals(Type.FIRE);
            case FLYINIUM_Z -> move.getType().equals(Type.FLYING);
            case GHOSTIUM_Z -> move.getType().equals(Type.GHOST);
            case GRASSIUM_Z -> move.getType().equals(Type.GRASS);
            case GROUNDIUM_Z -> move.getType().equals(Type.GROUND);
            case ICIUM_Z -> move.getType().equals(Type.ICE);
            case NORMALIUM_Z -> move.getType().equals(Type.NORMAL);
            case POISONIUM_Z -> move.getType().equals(Type.POISON);
            case PSYCHIUM_Z -> move.getType().equals(Type.PSYCHIC);
            case ROCKIUM_Z -> move.getType().equals(Type.ROCK);
            case STEELIUM_Z -> move.getType().equals(Type.STEEL);
            case WATERIUM_Z -> move.getType().equals(Type.WATER);
            //Uniques
            case ALORAICHIUM_Z -> pokemonName.equals("Alolan Raichu") && move.getName().equals("Thunderbolt");
            case DECIDIUM_Z -> pokemonName.equals("Decidueye") && move.getName().equals("Spirit Shackle");
            case EEVIUM_Z -> pokemonName.equals("Eevee") && move.getName().equals("Last Resort");
            case INCINIUM_Z -> pokemonName.equals("Incineroar") && move.getName().equals("Darkest Lariat");
            case KOMMOIUM_Z -> pokemonName.equals("Kommo O") && move.getName().equals("Clanging Scales");
            case LUNALIUM_Z -> (pokemonName.equals("Lunala") || pokemonName.equals("Dawn Wings Necrozma")) && move.getName().equals("Moongeist Beam");
            case LYCANIUM_Z -> (pokemonName.equals("Lycanroc") || pokemonName.equals("Lycanroc Day") || pokemonName.equals("Lycanroc Night")) && move.getName().equals("Stone Edge");
            case MARSHADIUM_Z -> pokemonName.equals("Marshadow") && move.getName().equals("Spectral Thief");
            case MEWNIUM_Z -> pokemonName.equals("Mew") && move.getName().equals("Psychic");
            case MIMIKIUM_Z -> pokemonName.equals("Mimikyu") && move.getName().equals("Play Rough");
            case PIKANIUM_Z -> pokemonName.equals("Pikachu") && move.getName().equals("Volt Tackle");
            case PIKASHUNIUM_Z -> pokemonName.equals("Pikachu") && move.getName().equals("Thunderbolt");
            case PRIMARIUM_Z -> pokemonName.equals("Primarina") && move.getName().equals("Sparkling Aria");
            case SNORLIUM_Z -> pokemonName.equals("Snorlax") && move.getName().equals("Giga Impact");
            case SOLGANIUM_Z -> (pokemonName.equals("Solgaleo") || pokemonName.equals("Dusk Mane Necrozma")) && move.getName().equals("Sunsteel Strike");
            case TAPUNIUM_Z -> pokemonName.contains("Tapu") && move.getName().equals("Natures Madness");
            case ULTRANECROZIUM_Z -> pokemonName.equals("Ultra Necrozma") && (move.getName().equals("Photon Geyser") || move.getName().equals("Prismatic Laser"));
            //Custom Uniques
            case RESHIRIUM_Z -> pokemonName.equals("Reshiram") && move.getName().equals("Blue Flare");
            case ZEKRIUM_Z -> pokemonName.equals("Zekrom") && move.getName().equals("Bolt Strike");
            case KYURIUM_Z -> (pokemonName.equals("Kyurem") && move.getName().equals("Glaciate")) || (pokemonName.equals("Black Kyurem") && move.getName().equals("Freeze Shock")) || (pokemonName.equals("Ice Burn") && move.getName().equals("Ice Burn"));
            case XERNIUM_Z -> pokemonName.equals("Xerneas") && move.getName().equals("Geomancy");
            case YVELTIUM_Z -> pokemonName.equals("Yveltal") && move.getName().equals("Oblivion Wing");
            case DIANCIUM_Z -> pokemonName.contains("Diancie") && move.getName().equals("Diamond Storm");
            case ARCEIUM_Z -> pokemonName.equals("Arceus") && move.getName().equals("Judgement");
            case RAYQUAZIUM_Z -> pokemonName.contains("Rayquaza") && move.getName().equals("Dragon Ascent");
            case ZYGARDIUM_Z -> (pokemonName.contains("Zygarde") && move.getName().equals("Lands Wrath")) || (pokemonName.contains("Complete") && (move.getName().equals("Core Enforcer") || move.getName().equals("Thousand Arrows") || move.getName().equals("Thousand Waves")));
            case VOLCANIUM_Z -> pokemonName.equals("Volcanion") && move.getName().equals("Steam Eruption");
            case KYOGRIUM_Z -> pokemonName.contains("Kyogre") && move.getName().equals("Origin Pulse");
            case GROUDONIUM_Z -> pokemonName.contains("Groudon") && move.getName().equals("Precipice Blades");
            case GENESECTIUM_Z -> pokemonName.equals("Genesect") && move.getName().equals("Techno Blast");
            case MELMETALIUM_Z -> pokemonName.equals("Melmetal") && (move.getName().equals("Double Iron Bash") || move.getName().equals("Acid Armor"));
            case DIALGIUM_Z -> pokemonName.equals("Dialga") && move.getName().equals("Roar Of Time");
            case PALKIUM_Z -> pokemonName.equals("Palkia") && move.getName().equals("Spacial Rend");
            case GIRATINIUM_Z -> pokemonName.contains("Giratina") && move.getName().equals("Shadow Force");
            case ETERNIUM_Z -> pokemonName.contains("Eternatus") && (move.getName().equals("Eternabeam") || move.getName().equals("Dynamax Cannon"));
            case DARKRAIUM_Z -> pokemonName.contains("Darkrai") && move.getName().equals("Dark Void");
        };
    }

    public static ZCrystal getRandomUniqueZCrystal()
    {
        List<ZCrystal> uniques = new ArrayList<>(Arrays.asList(values()).subList(18, values().length));
        Collections.shuffle(uniques);

        return uniques.get(0);
    }
}
