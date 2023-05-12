package com.calculusmaster.pokecord.game.enums.items;

import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.util.Global;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.calculusmaster.pokecord.game.moves.data.MoveEntity.*;

public enum TM
{
    TM001 (TAKE_DOWN),
    TM002 (CHARM),
    TM003 (FAKE_TEARS),
    TM004 (AGILITY),
    TM005 (MUD_SLAP),
    TM006 (SCARY_FACE),
    TM007 (PROTECT),
    TM008 (FIRE_FANG),
    TM009 (THUNDER_FANG),
    TM010 (ICE_FANG),
    TM011 (WATER_PULSE),
    TM012 (LOW_KICK),
    TM013 (ACID_SPRAY),
    TM014 (ACROBATICS),
    TM015 (STRUGGLE_BUG),
    TM016 (PSYBEAM),
    TM017 (CONFUSE_RAY),
    TM018 (THIEF),
    TM019 (DISARMING_VOICE),
    TM020 (TRAILBLAZE),
    TM021 (POUNCE),
    TM022 (CHILLING_WATER),
    TM023 (CHARGE_BEAM),
    TM024 (FIRE_SPIN),
    TM025 (FACADE),
    TM026 (POISON_TAIL),
    TM027 (AERIAL_ACE),
    TM028 (BULLDOZE),
    TM029 (HEX),
    TM030 (SNARL),
    TM031 (METAL_CLAW),
    TM032 (SWIFT),
    TM033 (MAGICAL_LEAF),
    TM034 (ICY_WIND),
    TM035 (MUD_SHOT),
    TM036 (ROCK_TOMB),
    TM037 (DRAINING_KISS),
    TM038 (FLAME_CHARGE),
    TM039 (LOW_SWEEP),
    TM040 (AIR_CUTTER),
    TM041 (STORED_POWER),
    TM042 (NIGHT_SHADE),
    TM043 (FLING),
    TM044 (DRAGON_TAIL),
    TM045 (VENOSHOCK),
    TM046 (AVALANCHE),
    TM047 (ENDURE),
    TM048 (VOLT_SWITCH),
    TM049 (SUNNY_DAY),
    TM050 (RAIN_DANCE),
    TM051 (SANDSTORM),
    TM052 (SNOWSCAPE),
    TM053 (SMART_STRIKE),
    TM054 (PSYSHOCK),
    TM055 (DIG),
    TM056 (BULLET_SEED),
    TM057 (FALSE_SWIPE),
    TM058 (BRICK_BREAK),
    TM059 (ZEN_HEADBUTT),
    TM060 (U_TURN),
    TM061 (SHADOW_CLAW),
    TM062 (FOUL_PLAY),
    TM063 (PSYCHIC_FANGS),
    TM064 (BULK_UP),
    TM065 (AIR_SLASH),
    TM066 (BODY_SLAM),
    TM067 (FIRE_PUNCH),
    TM068 (THUNDER_PUNCH),
    TM069 (ICE_PUNCH),
    TM070 (SLEEP_TALK),
    TM071 (SEED_BOMB),
    TM072 (ELECTRO_BALL),
    TM073 (DRAIN_PUNCH),
    TM074 (REFLECT),
    TM075 (LIGHT_SCREEN),
    TM076 (ROCK_BLAST),
    TM077 (WATERFALL),
    TM078 (DRAGON_CLAW),
    TM079 (DAZZLING_GLEAM),
    TM080 (METRONOME),
    TM081 (GRASS_KNOT),
    TM082 (THUNDER_WAVE),
    TM083 (POISON_JAB),
    TM084 (STOMPING_TANTRUM),
    TM085 (REST),
    TM086 (ROCK_SLIDE),
    TM087 (TAUNT),
    TM088 (SWORDS_DANCE),
    TM089 (BODY_PRESS),
    TM090 (SPIKES),
    TM091 (TOXIC_SPIKES),
    TM092 (IMPRISON),
    TM093 (FLASH_CANNON),
    TM094 (DARK_PULSE),
    TM095 (LEECH_LIFE),
    TM096 (EERIE_IMPULSE),
    TM097 (FLY),
    TM098 (SKILL_SWAP),
    TM099 (IRON_HEAD),
    TM100 (DRAGON_DANCE),
    TM101 (POWER_GEM),
    TM102 (GUNK_SHOT),
    TM103 (SUBSTITUTE),
    TM104 (IRON_DEFENSE),
    TM105 (X_SCISSOR),
    TM106 (DRILL_RUN),
    TM107 (WILL_O_WISP),
    TM108 (CRUNCH),
    TM109 (TRICK),
    TM110 (LIQUIDATION),
    TM111 (GIGA_DRAIN),
    TM112 (AURA_SPHERE),
    TM113 (TAILWIND),
    TM114 (SHADOW_BALL),
    TM115 (DRAGON_PULSE),
    TM116 (STEALTH_ROCK),
    TM117 (HYPER_VOICE),
    TM118 (HEAT_WAVE),
    TM119 (ENERGY_BALL),
    TM120 (PSYCHIC),
    TM121 (HEAVY_SLAM),
    TM122 (ENCORE),
    TM123 (SURF),
    TM124 (ICE_SPINNER),
    TM125 (FLAMETHROWER),
    TM126 (THUNDERBOLT),
    TM127 (PLAY_ROUGH),
    TM128 (AMNESIA),
    TM129 (CALM_MIND),
    TM130 (HELPING_HAND),
    TM131 (POLLEN_PUFF),
    TM132 (BATON_PASS),
    TM133 (EARTH_POWER),
    TM134 (REVERSAL),
    TM135 (ICE_BEAM),
    TM136 (ELECTRIC_TERRAIN),
    TM137 (GRASSY_TERRAIN),
    TM138 (PSYCHIC_TERRAIN),
    TM139 (MISTY_TERRAIN),
    TM140 (NASTY_PLOT),
    TM141 (FIRE_BLAST),
    TM142 (HYDRO_PUMP),
    TM143 (BLIZZARD),
    TM144 (FIRE_PLEDGE),
    TM145 (WATER_PLEDGE),
    TM146 (GRASS_PLEDGE),
    TM147 (WILD_CHARGE),
    TM148 (SLUDGE_BOMB),
    TM149 (EARTHQUAKE),
    TM150 (STONE_EDGE),
    TM151 (PHANTOM_FORCE),
    TM152 (GIGA_IMPACT),
    TM153 (BLAST_BURN),
    TM154 (HYDRO_CANNON),
    TM155 (FRENZY_PLANT),
    TM156 (OUTRAGE),
    TM157 (OVERHEAT),
    TM158 (FOCUS_BLAST),
    TM159 (LEAF_STORM),
    TM160 (HURRICANE),
    TM161 (TRICK_ROOM),
    TM162 (BUG_BUZZ),
    TM163 (HYPER_BEAM),
    TM164 (BRAVE_BIRD),
    TM165 (FLARE_BLITZ),
    TM166 (THUNDER),
    TM167 (CLOSE_COMBAT),
    TM168 (SOLAR_BEAM),
    TM169 (DRACO_METEOR),
    TM170 (STEEL_BEAM),
    TM171 (TERA_BLAST),

    ;

    private static final Map<MoveEntity, TM> MOVE_TO_TM_MAP = new HashMap<>();
    static { Arrays.stream(TM.values()).forEach(tm -> MOVE_TO_TM_MAP.put(tm.getMove(), tm)); }

    private final MoveEntity moveEntity;
    TM(MoveEntity moveEntity)
    {
        this.moveEntity = moveEntity;
    }

    public MoveEntity getMove()
    {
        return this.moveEntity;
    }

    public static TM cast(String str)
    {
        TM tm = Global.getEnumFromString(TM.values(), str);
        if(tm == null) tm = Arrays.stream(TM.values()).filter(tmEnum -> tmEnum.getMove().equals(MoveEntity.cast(str))).findFirst().orElse(null);
        return tm;
    }

    public static boolean isMoveTM(MoveEntity move)
    {
        return Arrays.stream(TM.values()).anyMatch(tm -> tm.getMove().equals(move));
    }

    public static TM getTM(MoveEntity move)
    {
        return MOVE_TO_TM_MAP.get(move);
    }
}
