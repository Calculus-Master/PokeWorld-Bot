package com.calculusmaster.pokecord.game.pokemon.component;

import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import org.bson.Document;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

//Custom Attributes
public class CustomPokemonData
{
    //Colors for Flabebe/Floette/Florges and Minior
    private static final String COLOR_BLUE = "Blue";
    private static final String COLOR_ORANGE = "Orange";
    private static final String COLOR_WHITE = "White";
    private static final String COLOR_YELLOW = "Yellow";
    private static final String COLOR_GREEN = "Green";
    private static final String COLOR_INDIGO = "Indigo";
    private static final String COLOR_RED = "Red";
    private static final String COLOR_VIOLET = "Violet";

    //East/West for Shellos/Gastrodon
    private static final String EAST = "EAST";
    private static final String WEST = "WEST";

    //Instance vars

    private final Random random;

    private String flowerColor; //Flabebe/Floette/Florges

    private String miniorColor; //Minior

    private boolean isOriginalColor; //Magearna

    private String slugDirection; //Shellos/Gastrodon

    public CustomPokemonData()
    {
        this.random = new Random();

        this.flowerColor = "";

        this.miniorColor = "";

        this.isOriginalColor = false;

        this.slugDirection = "";
    }

    public CustomPokemonData(Document data)
    {
        this();

        if(data.containsKey("flowerColor")) this.flowerColor = data.getString("flowerColor");

        if(data.containsKey("miniorColor")) this.miniorColor = data.getString("miniorColor");

        if(data.containsKey("isOriginalColor")) this.isOriginalColor = data.getBoolean("isOriginalColor");

        if(data.containsKey("slugDirection")) this.slugDirection = data.getString("slugDirection");
    }

    public Document serialize()
    {
        Document data = new Document();

        if(!this.flowerColor.isEmpty()) data.append("flowerColor", this.flowerColor);

        if(!this.miniorColor.isEmpty()) data.append("miniorColor", this.miniorColor);

        if(this.isOriginalColor) data.append("isOriginalColor", true);

        if(!this.slugDirection.isEmpty()) data.append("slugDirection", this.slugDirection);

        return data;
    }

    public CustomPokemonData generateOnSpawn(PokemonEntity entity)
    {
        if(EnumSet.of(PokemonEntity.FLABEBE, PokemonEntity.FLOETTE, PokemonEntity.FLORGES).contains(entity))
        {
            List<String> pool = List.of(COLOR_BLUE, COLOR_ORANGE, COLOR_WHITE, COLOR_YELLOW);
            this.flowerColor = pool.get(this.random.nextInt(pool.size()));
        }
        else if(EnumSet.of(PokemonEntity.MINIOR_METEOR, PokemonEntity.MINIOR_CORE).contains(entity))
        {
            List<String> pool = List.of(COLOR_RED, COLOR_ORANGE, COLOR_YELLOW, COLOR_GREEN, COLOR_BLUE, COLOR_INDIGO, COLOR_VIOLET);
            this.miniorColor = pool.get(this.random.nextInt(pool.size()));
        }
        else if(entity == PokemonEntity.MAGEARNA && this.random.nextFloat() < 0.02)
            this.isOriginalColor = true;
        else if(entity == PokemonEntity.SHELLOS || entity == PokemonEntity.GASTRODON)
            this.slugDirection = this.random.nextBoolean() ? EAST : WEST;

        return this;
    }

    //Individual Data Methods
    public String getMiniorCoreColor()
    {
        return this.miniorColor;
    }

    public String getFlowerColor()
    {
        return this.flowerColor;
    }

    public boolean isOriginalColorMagearna()
    {
        return this.isOriginalColor;
    }

    public String getSlugDirection()
    {
        return this.slugDirection;
    }
}
