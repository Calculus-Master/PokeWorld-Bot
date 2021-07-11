package com.calculusmaster.pokecord.game.enums.items;

import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.util.interfaces.ZCrystalValidator;

import java.util.*;

public enum ZCrystal
{
    BUGINIUM_Z(Type.BUG),
    DARKINIUM_Z(Type.DARK),
    DRAGONIUM_Z(Type.DRAGON),
    ELECTRIUM_Z(Type.ELECTRIC),
    FAIRIUM_Z(Type.FAIRY),
    FIGHTINIUM_Z(Type.FIGHTING),
    FIRIUM_Z(Type.FIRE),
    FLYINIUM_Z(Type.FLYING),
    GHOSTIUM_Z(Type.GHOST),
    GRASSIUM_Z(Type.GRASS),
    GROUNDIUM_Z(Type.GROUND),
    ICIUM_Z(Type.ICE),
    NORMALIUM_Z(Type.NORMAL),
    POISONIUM_Z(Type.POISON),
    PSYCHIUM_Z(Type.PSYCHIC),
    ROCKIUM_Z(Type.ROCK),
    STEELIUM_Z(Type.STEEL),
    WATERIUM_Z(Type.WATER),
    ALORAICHIUM_Z("Alolan Raichu", "Thunderbolt"),
    DECIDIUM_Z("Decidueye", "Spirit Shackle"),
    EEVIUM_Z("Eevee", "Last Resort"),
    INCINIUM_Z("Incineroar", "Darkest Lariat"),
    KOMMOIUM_Z("Kommo O", "Clanging Scales"),
    LUNALIUM_Z((p, m) -> (p.equals("Lunala") || p.equals("Dawn Wings Necrozma")) && m.getName().equals("Moongeist Beam")),
    LYCANIUM_Z("Lycanroc", "Stone Edge"),
    MARSHADIUM_Z("Marshadow", "Spectral Thief"),
    MEWNIUM_Z("Mew", "Psychic"),
    MIMIKIUM_Z("Mimikyu", "Play Rough"),
    PIKANIUM_Z("Pikachu", "Volt Tackle"),
    PIKASHUNIUM_Z("Pikachu", "Thunderbolt"),
    PRIMARIUM_Z("Primarina", "Sparkling Aria"),
    SNORLIUM_Z("Snorlax", "Giga Impact"),
    SOLGANIUM_Z((p, m) -> (p.equals("Solgaleo") || p.equals("Dusk Mane Necrozma")) && m.getName().equals("Sunsteel Strike")),
    TAPUNIUM_Z("Tapu", "Natures Madness"),
    ULTRANECROZIUM_Z("Ultra Necrozma", "Photon Geyser", "Prismatic Laser"),
    //Custom Z-Crystals
    RESHIRIUM_Z("Reshiram", "Blue Flare"),
    ZEKRIUM_Z("Zekrom", "Bolt Strike"),
    KYURIUM_Z((p, m) -> (p.equals("Kyurem") && m.getName().equals("Glaciate")) || (p.equals("Black Kyurem") && m.getName().equals("Freeze Shock")) || (p.equals("White Kyurem") && m.getName().equals("Ice Burn"))),
    XERNIUM_Z("Xerneas", "Geomancy"),
    YVELTIUM_Z("Yveltal", "Oblivion Wing"),
    DIANCIUM_Z("Diancie", "Diamond Storm"),
    ARCEIUM_Z("Arceus", "Judgement"),
    RAYQUAZIUM_Z("Rayquaza", "Dragon Ascent"),
    ZYGARDIUM_Z("Zygarde", "Lands Wrath", "Core Enforcer", "Thousand Arrows", "Thousand Waves"),
    VOLCANIUM_Z("Volcanion", "Steam Eruption"),
    KYOGRIUM_Z("Kyogre", "Origin Pulse"),
    GROUDONIUM_Z("Groudon", "Precipice Blades"),
    GENESECTIUM_Z("Genesect", "Techno Blast"),
    MELMETALIUM_Z("Melmetal", "Double Iron Bash", "Acid Armor"),
    DIALGIUM_Z("Dialga", "Roar Of Time"),
    PALKIUM_Z("Palkia", "Spacial Rend"),
    GIRATINIUM_Z("Giratina", "Shadow Force"),
    ETERNIUM_Z("Eternatus", "Eternabeam", "Dynamax Cannon"),
    DARKRAIUM_Z("Darkrai", "Dark Void");

    private ZCrystalValidator rule;
    private Type type;

    ZCrystal(ZCrystalValidator rule)
    {
        this.rule = rule;
        this.type = null;
    }

    ZCrystal(Type t)
    {
        this((p, m) -> m.getType().equals(t));
        this.type = t;
    }

    ZCrystal(String pokemonName, String... moveNames)
    {
        this((p, m) -> p.contains(pokemonName) && Arrays.asList(moveNames).contains(m.getName()));
    }

    public boolean check(String pokemonName, Move move)
    {
        return this.rule.check(pokemonName, move);
    }

    public Type getType()
    {
        return this.type;
    }

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
        for(ZCrystal z : ZCrystal.values()) if(z.getType().equals(t)) return z;
        return null;
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

        return z.check(pokemonName, move);
    }

    public static ZCrystal getRandomUniqueZCrystal()
    {
        List<ZCrystal> uniques = new ArrayList<>(Arrays.asList(values()).subList(18, values().length));
        Collections.shuffle(uniques);

        return uniques.get(0);
    }
}
