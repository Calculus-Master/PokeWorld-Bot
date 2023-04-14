package com.calculusmaster.pokecord.game.objectives.types;

import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MoveListObjective extends AbstractObjective
{
    private List<MoveEntity> list;

    @Override
    public void read(Document data)
    {
        super.read(data);
        this.list = data.getList("list", String.class).stream().map(MoveEntity::valueOf).toList();
    }

    @Override
    public Document serialize()
    {
        return super.serialize().append("list", this.list.stream().map(MoveEntity::toString).toList());
    }

    @Override
    public AbstractObjective generate()
    {
        int listSize = new Random().nextInt(6) + 5;

        this.list = new ArrayList<>();
        IntStream.range(0, listSize).forEach(i -> this.list.add(MoveEntity.getRandom()));

        return this;
    }

    @Override
    protected String getSpecificDescription()
    {
        return "Moves: " + this.list.stream().map(MoveEntity::getName).collect(Collectors.joining(", ")) + ".";
    }

    public List<MoveEntity> getList()
    {
        return this.list;
    }

    public void setList(List<MoveEntity> list)
    {
        this.list = list;
    }
}
