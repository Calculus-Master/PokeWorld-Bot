package com.calculusmaster.pokecord.game.enums.items;

public enum XPBooster
{
    XP_30_MIN(10, 2.0),
    XP_1_HR(50, 2.0),
    XP_2_HR(100, 1.5),
    XP_3_HR(200, 1.5),
    XP_4_HR(250, 1.5),
    XP_24_HR(1000, 1.25);

    public int price;
    public double boost;
    XPBooster(int price, double boost)
    {
        this.price = price;
        this.boost = boost;
    }

    /**
     * Returns the booster time
     * @return Booster time in seconds
     */
    public int time()
    {
        int hours = this.toString().contains("HR") ? Integer.parseInt(this.toString().split("_")[1]) : 0;
        int minute = this.toString().contains("MIN") ? Integer.parseInt(this.toString().split("_")[1]) : 0;

        return hours * 60 + minute;
    }

    public String timeForShop()
    {
        String[] split = this.toString().split("_");
        return split[1] + " " + split[2];
    }

    public static XPBooster getInstance(int length)
    {
        for(XPBooster xp : values()) if(xp.time() == length) return xp;
        return null;
    }
}
