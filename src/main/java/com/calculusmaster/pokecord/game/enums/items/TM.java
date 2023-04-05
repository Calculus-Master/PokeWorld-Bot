package com.calculusmaster.pokecord.game.enums.items;

import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.util.Global;

import java.util.Arrays;

import static com.calculusmaster.pokecord.game.moves.data.MoveEntity.*;

public enum TM
{
    //TODO: update to Scarlet Violet TMs, and use Move Entity instead of String
    TM001(TAKE_DOWN),
    TM002(CHARM),
    TM003(FAKE_TEARS),
    TM004(AGILITY),
    TM005(MUD_SLAP),
    TM006(SCARY_FACE),
    TM007(PROTECT),
    TM008(FIRE_FANG),
    TM009(THUNDER_FANG),
    TM010(ICE_FANG),
    TM011(WATER_PULSE),
    TM012(LOW_KICK),
    TM013(ACID_SPRAY),
    TM014(ACROBATICS),
    TM015(STRUGGLE_BUG),

    ;

    private final MoveEntity moveEntity;
    TM(MoveEntity moveEntity)
    {
        this.moveEntity = moveEntity;
    }

    public MoveEntity getMove()
    {
        return this.moveEntity;
    }

    public String getShopEntry()
    {
        return "`" + this + "` - " + this.getMove().data().getName();
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
}
