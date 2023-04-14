package com.calculusmaster.pokecord.game.objectives.types;

import com.calculusmaster.pokecord.game.objectives.ObjectiveType;
import org.bson.Document;

public abstract class AbstractObjective
{
    protected ObjectiveType objectiveType;
    protected int progress;
    protected int target;

    public AbstractObjective()
    {
        this.objectiveType = null;
        this.progress = 0;
        this.target = 0;
    }

    public void read(Document data)
    {
        this.objectiveType = ObjectiveType.valueOf(data.get("objective_type", String.class));
        this.progress = data.getInteger("progress");
        this.target = data.getInteger("target");
    }

    public Document serialize()
    {
        return new Document()
                .append("objective_type", this.objectiveType.toString())
                .append("progress", this.progress)
                .append("target", this.target);
    }

    public abstract AbstractObjective generate();
    protected abstract String getSpecificDescription();

    public String getDescription()
    {
        return "*" + this.getObjectiveType().getDescription() + "* " + this.getSpecificDescription();
    }

    public String getStatus()
    {
        return this.isComplete() ? "*Complete*" : this.progress + " / " + this.target;
    }

    public boolean isComplete()
    {
        return this.progress >= this.target;
    }

    //Setters / Getters
    public void setObjectiveType(ObjectiveType objectiveType)
    {
        this.objectiveType = objectiveType;
    }

    public AbstractObjective setTarget(int target)
    {
        this.target = target;
        return this;
    }

    public AbstractObjective setProgress(int progress)
    {
        this.progress = progress;
        return this;
    }

    public int getProgress()
    {
        return this.progress;
    }

    public int getTarget()
    {
        return this.target;
    }

    public ObjectiveType getObjectiveType()
    {
        return this.objectiveType;
    }

    //Casts
    public CategoryObjective asCategory()
    {
        return (CategoryObjective)this;
    }
    public PokemonListObjective asPokemonList()
    {
        return (PokemonListObjective)this;
    }
    public MoveListObjective asMoveList()
    {
        return (MoveListObjective)this;
    }
    public StatObjective asStat()
    {
        return (StatObjective)this;
    }
    public TypeObjective asType()
    {
        return (TypeObjective)this;
    }
    public PowerObjective asPower()
    {
        return (PowerObjective)this;
    }
    public PokemonSpecificObjective asPokemon()
    {
        return (PokemonSpecificObjective)this;
    }
    public MoveSpecificObjective asMove()
    {
        return (MoveSpecificObjective)this;
    }
}
