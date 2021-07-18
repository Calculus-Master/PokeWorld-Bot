package com.calculusmaster.pokecord.game;

public enum PokemonSkin
{
    ULTRA_NECROZMA_GREEN("Ultra Necrozma", "Green Ultra Necrozma", "https://i.ibb.co/6nQXzNt/Ultra-Necrozma-Green.png");

    public String pokemon, skinName, URL;
    PokemonSkin(String pokemon, String skinName, String URL)
    {
        this.pokemon = pokemon;
        this.skinName = skinName;
        this.URL = URL;
    }

    public static PokemonSkin cast(String s)
    {
        for(PokemonSkin skin : values()) if(s.equalsIgnoreCase(skin.toString()) || s.equalsIgnoreCase(skin.skinName)) return skin;
        return null;
    }
}
