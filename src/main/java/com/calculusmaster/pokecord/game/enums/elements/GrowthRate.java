package com.calculusmaster.pokecord.game.enums.elements;

import com.calculusmaster.pokecord.util.Global;

public enum GrowthRate
{
    ERRATIC(15), FAST(6), MEDIUM_FAST(8), MEDIUM_SLOW(9), SLOW(10), FLUCTUATING(4);

    public int init;

    GrowthRate(int init)
    {
        this.init = init;
    }

    public static GrowthRate cast(String growthRate)
    {
        return (GrowthRate) Global.getEnumFromString(values(), growthRate);
    }

    public static int getRequiredExp(String rate, int level)
    {
        return level == 1 ? cast(rate).init : totalRequiredEXP(cast(rate), level) - totalRequiredEXP(cast(rate), level - 1);
    }

    private static int totalRequiredEXP(GrowthRate g, int level)
    {
        return (int) (g.equals(ERRATIC) ? (level + 1 <= 50 ? Math.pow(level + 1, 3) * (100.0 - (level + 1)) / 50.0 : (level + 1 <= 68 ? Math.pow(level + 1, 3) * (150.0 - (level + 1)) / 100.0 : (level + 1 <= 98 ? Math.pow(level + 1, 3) * ((1911 - 10 * (level + 1)) / 3.0) / 500.0 : Math.pow(level + 1, 3) * (160.0 - (level + 1)) / 100.0))) : (g.equals(FAST) ? ((4.0 / 5.0) * Math.pow(level + 1, 3)) : (g.equals(MEDIUM_FAST) ? (Math.pow(level + 1, 3)) : (g.equals(MEDIUM_SLOW) ? ((6.0 / 5.0) * Math.pow(level + 1, 3) - 15.0 * Math.pow(level + 1, 2) + 100.0 * (level + 1) - 140.0) : (g.equals(SLOW) ? ((5.0 / 4.0) * Math.pow(level + 1, 3)) : (g.equals(FLUCTUATING) ? (level + 1 <= 15 ? Math.pow(level + 1, 3) * ((24 + (level + 2) / 3.0) / 50.0) : (level + 1 <= 36 ? Math.pow(level + 1, 3) * ((level + 15) / 50.0) : Math.pow(level + 1, 3) * ((32 + (level + 1) / 2.0) / 50.0))) : Double.NaN))))));
    }
}
