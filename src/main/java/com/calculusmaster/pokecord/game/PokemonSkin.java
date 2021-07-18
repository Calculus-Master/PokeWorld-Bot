package com.calculusmaster.pokecord.game;

public enum PokemonSkin
{
    ULTRA_NECROZMA_GREEN("Ultra Necrozma", "https://i.ibb.co/6nQXzNt/Ultra-Necrozma-Green.png");

    public String pokemon, URL;
    PokemonSkin(String pokemon, String URL)
    {
        this.pokemon = pokemon;
        this.URL = URL;
    }

    public static PokemonSkin cast(String s)
    {
        for(PokemonSkin skin : values()) if(s.equalsIgnoreCase(skin.toString())) return skin;
        return null;
    }
}
