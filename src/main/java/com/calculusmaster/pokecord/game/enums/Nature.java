package com.calculusmaster.pokecord.game.enums;

import com.calculusmaster.pokecord.util.Global;

public enum Nature
{
    HARDY, LONELY, BRAVE, ADAMANT, NAUGHTY, BOLD, DOCILE, RELAXED, IMPISH, LAX, TIMID, HASTY, SERIOUS, JOLLY, NAIVE, MODEST, MILD, QUIET, BASHFUL, RASH, CALM, GENTLE, SASSY, CAREFUL, QUIRKY;

    public static Nature cast(String nature)
    {
        return (Nature) Global.getEnumFromString(values(), nature);
    }
}
