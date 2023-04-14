package com.calculusmaster.pokecord.game.objectives.types;

import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PokemonListObjective extends AbstractObjective
{
    private List<PokemonEntity> list;

    @Override
    public void read(Document data)
    {
        super.read(data);
        this.list = data.getList("list", String.class).stream().map(PokemonEntity::valueOf).toList();
    }

    @Override
    public Document serialize()
    {
        return super.serialize().append("list", this.list.stream().map(PokemonEntity::toString).toList());
    }

    @Override
    public AbstractObjective generate()
    {
        int listSize = new Random().nextInt(6) + 5;

        this.list = new ArrayList<>();
        IntStream.range(0, listSize).forEach(i -> this.list.add(PokemonEntity.getRandom()));

        return this;
    }

    @Override
    protected String getSpecificDescription()
    {
        return "Pokemon: " + this.list.stream().map(PokemonEntity::getName).collect(Collectors.joining(", ")) + ".";
    }

    public List<PokemonEntity> getList()
    {
        return this.list;
    }

    public void setList(List<PokemonEntity> list)
    {
        this.list = list;
    }
}
