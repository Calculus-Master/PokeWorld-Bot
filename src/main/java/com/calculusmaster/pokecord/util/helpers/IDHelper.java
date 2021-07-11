package com.calculusmaster.pokecord.util.helpers;

import java.util.Random;

public class IDHelper
{
    private static final String DIGIT_POOL = "0123456789";
    private static final String ALPHA_POOL = "abcdefghijklmnopqrstuvwxyz" + DIGIT_POOL;

    public static String numeric(int digits)
    {
        return generate(DIGIT_POOL, digits);
    }

    public static String alphanumeric(int digits)
    {
        return generate(ALPHA_POOL, digits);
    }

    private static String generate(String pool, int digits)
    {
        Random r = new Random();
        StringBuilder ID = new StringBuilder();

        for(int i = 0; i < digits; i++) ID.append(pool.charAt(r.nextInt(pool.length())));

        return ID.toString();
    }
}
