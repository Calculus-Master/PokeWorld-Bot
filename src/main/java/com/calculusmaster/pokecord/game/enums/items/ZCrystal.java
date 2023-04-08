package com.calculusmaster.pokecord.game.enums.items;

import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.interfaces.ZCrystalValidator;
import org.jooq.lambda.Seq;

import java.util.*;

import static com.calculusmaster.pokecord.game.moves.data.MoveEntity.*;
import static com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity.*;

public enum ZCrystal
{
    //Typed
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
    //Unique
    ALORAICHIUM_Z(RAICHU_ALOLA, THUNDERBOLT),
    DECIDIUM_Z(DECIDUEYE, SPIRIT_SHACKLE),
    EEVIUM_Z(EEVEE, LAST_RESORT),
    INCINIUM_Z(INCINEROAR, DARKEST_LARIAT),
    KOMMOIUM_Z(KOMMO_O, CLANGING_SCALES),
    LUNALIUM_Z(EnumSet.of(LUNALA, NECROZMA_DAWN_WINGS), MOONGEIST_BEAM),
    LYCANIUM_Z(EnumSet.of(LYCANROC_DUSK, LYCANROC_MIDDAY, LYCANROC_MIDNIGHT), STONE_EDGE),
    MARSHADIUM_Z(MARSHADOW, SPECTRAL_THIEF),
    MEWNIUM_Z(MEW, PSYCHIC),
    MIMIKIUM_Z(MIMIKYU, PLAY_ROUGH),
    PIKANIUM_Z(PIKACHU, VOLT_TACKLE),
    PIKASHUNIUM_Z(PIKACHU, THUNDERBOLT),
    PRIMARIUM_Z(PRIMARINA, SPARKLING_ARIA),
    SNORLIUM_Z(SNORLAX, GIGA_IMPACT),
    SOLGANIUM_Z(EnumSet.of(SOLGALEO, NECROZMA_DUSK_MANE), SUNSTEEL_STRIKE),
    TAPUNIUM_Z(EnumSet.of(TAPU_LELE, TAPU_BULU, TAPU_FINI, TAPU_KOKO), NATURES_MADNESS),
    ULTRANECROZIUM_Z(NECROZMA_ULTRA, PHOTON_GEYSER, PRISMATIC_LASER),
    //Custom Z-Crystals
    RESHIRIUM_Z(RESHIRAM, BLUE_FLARE),
    ZEKRIUM_Z(ZEKROM, BOLT_STRIKE),
    KYURIUM_Z((p, m) -> (p == KYUREM && m.is(GLACIATE)) || (p == KYUREM_BLACK && m.is(FREEZE_SHOCK)) || (p == KYUREM_WHITE && m.is(ICE_BURN))),
    XERNIUM_Z(XERNEAS, GEOMANCY),
    YVELTIUM_Z(YVELTAL, OBLIVION_WING),
    DIANCIUM_Z(DIANCIE, DIAMOND_STORM),
    ARCEIUM_Z(ARCEUS, JUDGMENT),
    RAYQUAZIUM_Z(EnumSet.of(RAYQUAZA, RAYQUAZA_MEGA), DRAGON_ASCENT),
    ZYGARDIUM_Z(EnumSet.of(ZYGARDE_10, ZYGARDE_50, ZYGARDE_COMPLETE), LANDS_WRATH, CORE_ENFORCER, THOUSAND_WAVES, THOUSAND_ARROWS),
    VOLCANIUM_Z(VOLCANION, STEAM_ERUPTION),
    KYOGRIUM_Z(EnumSet.of(KYOGRE, KYOGRE_PRIMAL), ORIGIN_PULSE),
    GROUDONIUM_Z(EnumSet.of(GROUDON, GROUDON_PRIMAL), PRECIPICE_BLADES),
    GENESECTIUM_Z(GENESECT, TECHNO_BLAST),
    MELMETALIUM_Z(MELMETAL, DOUBLE_IRON_BASH, ACID_ARMOR),
    DIALGIUM_Z(EnumSet.of(DIALGA, DIALGA_ORIGIN), ROAR_OF_TIME),
    PALKIUM_Z(EnumSet.of(PALKIA, PALKIA_ORIGIN), SPACIAL_REND),
    GIRATINIUM_Z(EnumSet.of(GIRATINA_ALTERED, GIRATINA_ORIGIN), SHADOW_FORCE),
    ETERNIUM_Z(EnumSet.of(ETERNATUS, ETERNATUS_ETERNAMAX), ETERNABEAM, DYNAMAX_CANNON),
    DARKRAIUM_Z(DARKRAI, DARK_VOID);

    private final ZCrystalValidator rule;
    private Type type;

    ZCrystal(ZCrystalValidator rule)
    {
        this.rule = rule;
        this.type = null;
    }

    ZCrystal(Type t)
    {
        this((p, m) -> m.is(t));
        this.type = t;
    }

    ZCrystal(PokemonEntity pokemonEntity, MoveEntity... moveEntities)
    {
        this((p, m) -> p == pokemonEntity && List.of(moveEntities).contains(m.getEntity()));
    }

    ZCrystal(EnumSet<PokemonEntity> pokemonEntities, MoveEntity... moveEntities)
    {
        this((p, m) -> pokemonEntities.contains(p) && List.of(moveEntities).contains(m.getEntity()));
    }

    public boolean check(PokemonEntity pokemonEntity, Move move)
    {
        return this.rule.check(pokemonEntity, move);
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
        ZCrystal zc = Global.getEnumFromString(values(), z);
        if(zc == null) zc = Arrays.stream(values()).filter(c -> c.getStyledName().equalsIgnoreCase(z) || c.getStyledName().equalsIgnoreCase(z + " Z")).findFirst().orElse(null);
        return zc;
    }

    public static ZCrystal getCrystalOfType(Type t)
    {
        for(ZCrystal z : ZCrystal.values()) if(z.getType().equals(t)) return z;
        return null;
    }
    
    public static boolean isValid(ZCrystal z, Move move, PokemonEntity pokemonEntity)
    {
        Map<ZCrystal, EnumSet<MoveEntity>> statusOverride = new HashMap<>();
        statusOverride.put(ZCrystal.XERNIUM_Z, EnumSet.of(GEOMANCY));
        statusOverride.put(ZCrystal.DARKRAIUM_Z, EnumSet.of(DARK_VOID));
        statusOverride.put(ZCrystal.MELMETALIUM_Z, EnumSet.of(ACID_ARMOR));

        if(move.getCategory().equals(Category.STATUS))
            if(!statusOverride.containsKey(z) || !statusOverride.get(z).contains(move.getEntity()))
                return false;

        return z.check(pokemonEntity, move);
    }

    public static ZCrystal getRandomUniqueZCrystal()
    {
        return Seq.seq(Arrays.stream(values())).filter(z -> z.type == null).shuffle().findFirst().orElse(ZCrystal.NORMALIUM_Z);
    }
}
