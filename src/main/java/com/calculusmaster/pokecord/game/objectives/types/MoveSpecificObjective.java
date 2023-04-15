package com.calculusmaster.pokecord.game.objectives.types;

import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import org.bson.Document;

public class MoveSpecificObjective extends AbstractObjective
{
    private MoveEntity entity;

    @Override
    public void read(Document data)
    {
        super.read(data);
        this.entity = MoveEntity.valueOf(data.getString("entity"));
    }

    @Override
    public Document serialize()
    {
        return super.serialize().append("entity", this.entity.toString());
    }

    @Override
    public AbstractObjective generate()
    {
        this.entity = MoveEntity.getRandom();
        while(this.entity.isZMove() || this.entity.isMaxMove()) this.entity = MoveEntity.getRandom();
        return this;
    }

    @Override
    protected String getSpecificDescription()
    {
        return "Move: " + this.entity.getName() + ".";
    }

    public MoveEntity getEntity()
    {
        return this.entity;
    }

    public void setEntity(MoveEntity entity)
    {
        this.entity = entity;
    }
}
