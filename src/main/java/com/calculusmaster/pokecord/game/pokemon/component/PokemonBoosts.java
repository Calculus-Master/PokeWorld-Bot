package com.calculusmaster.pokecord.game.pokemon.component;

public class PokemonBoosts
{
    private double health;
    private double stat;

    public PokemonBoosts()
    {
        this(1.0, 1.0);
    }

    public PokemonBoosts(double health, double stat)
    {
        this.health = health;
        this.stat = stat;
    }

    public void setHealthBoost(double health)
    {
        this.health = health;
    }

    public void setStatBoost(double stat)
    {
        this.stat = stat;
    }

    public double getHealthBoost()
    {
        return this.health;
    }

    public double getStatBoost()
    {
        return this.stat;
    }
}
