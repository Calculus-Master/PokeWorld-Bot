package com.calculusmaster.pokecord.game.objectives.types;

import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import org.bson.Document;

public class PokemonSpecificObjective extends AbstractObjective
{
    private PokemonEntity entity;

    @Override
    public void read(Document data)
    {
        super.read(data);
        this.entity = PokemonEntity.valueOf(data.getString("entity"));
    }

    @Override
    public Document serialize()
    {
        return super.serialize().append("entity", this.entity.toString());
    }

    @Override
    public AbstractObjective generate()
    {
        this.entity = PokemonEntity.getRandom();
        return this;
    }

    @Override
    protected String getSpecificDescription()
    {
        return "Pokemon: " + this.entity.getName() + ".";
    }

    public PokemonEntity getEntity()
    {
        return this.entity;
    }

    public void setEntity(PokemonEntity entity)
    {
        this.entity = entity;
    }
}
