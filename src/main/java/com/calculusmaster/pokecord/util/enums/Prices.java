package com.calculusmaster.pokecord.util.enums;

public enum Prices
{
    TRIAL_DUEL(1500),
    SHOP_MEGA(2000),
    SHOP_FORM(1500),
    SHOP_NATURE(200),
    SHOP_CANDY(500),
    SHOP_MOVETUTOR(6000),
    SHOP_ZCRYSTAL(15000),
    SHOP_BASE_TM(3000),
    SHOP_RANDOM_TM(4000),
    PRESTIGE(20000);

    private int price;
    Prices(int price)
    {
        this.price = price;
    }

    public int get()
    {
        return this.price;
    }
}
