package com.calculusmaster.pokecord.game.bounties.components;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.*;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.helpers.IDHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.Random;

public class Bounty
{
    public static final int MAX_BOUNTIES_HELD = 3;
    public static final int POKEPASS_EXP_YIELD = 200;

    private String bountyID;
    private Objective objective;
    private int reward;

    public static Bounty create()
    {
        Bounty b = new Bounty();

        b.setBountyID(IDHelper.numeric(8));
        b.setRandomReward();
        b.setRandomObjective();

        return b;
    }

    public static Bounty fromDB(String bountyID)
    {
        Document d = Mongo.BountyData.find(Filters.eq("bountyID", bountyID)).first();

        Bounty b = new Bounty();

        b.setBountyID(bountyID);
        b.setReward(d.getInteger("reward"));
        b.setObjective(ObjectiveType.cast(d.getString("objective_type")))
                .setProgression(d.getInteger("progression"))
                .setTarget(d.getInteger("target"));

        if(b.objective instanceof DefeatTypeObjective o) o.setType(d.getString("type"));
        if(b.objective instanceof CatchTypeObjective o) o.setType(d.getString("type"));
        if(b.objective instanceof CatchNameObjective o) o.setName(d.getString("name"));
        if(b.objective instanceof EarnEVsStatObjective o) o.setStat(d.getString("stat"));
        if(b.objective instanceof DefeatPoolObjective o) o.setPool(d.getList("pool", String.class));
        if(b.objective instanceof CatchPoolObjective o) o.setPool(d.getList("pool", String.class));
        if(b.objective instanceof ReleaseTypeObjective o) o.setType(d.getString("type"));
        if(b.objective instanceof ReleaseNameObjective o) o.setName(d.getString("name"));
        if(b.objective instanceof ReleasePoolObjective o) o.setPool(d.getList("pool", String.class));
        if(b.objective instanceof UseZMoveTypeObjective o) o.setType(d.getString("type"));
        if(b.objective instanceof UseMaxMoveTypeObjective o) o.setType(d.getString("type"));
        if(b.objective instanceof UseMoveTypeObjective o) o.setType(d.getString("type"));
        if(b.objective instanceof UseMoveNameObjective o) o.setName(d.getString("name"));
        if(b.objective instanceof UseMoveCategoryObjective o) o.setCategory(d.getString("category"));
        if(b.objective instanceof UseMovePoolObjective o) o.setPool(d.getList("pool", String.class));
        if(b.objective instanceof UseMovePowerLessObjective o) o.setPower(d.getInteger("power"));
        if(b.objective instanceof UseMovePowerGreaterObjective o) o.setPower(d.getInteger("power"));
        if(b.objective instanceof UseMoveAccuracyLessObjective o) o.setAccuracy(d.getInteger("accuracy"));

        return b;
    }

    public static void toDB(Bounty b)
    {
        Document data = new Document()
                .append("bountyID", b.getBountyID())
                .append("reward", b.getReward());

        b.objective.addObjectiveData(data);

        Mongo.BountyData.insertOne(data);
    }

    public static void delete(String bountyID)
    {
        Mongo.BountyData.deleteOne(Filters.eq("bountyID", bountyID));
    }

    public String getOverview()
    {
        String overview = "ID: " + this.bountyID + "\nReward: " + this.getReward() + "c\n" + this.objective.getDesc() + "\n" + this.objective.getStatus();
        return (this.objective.isComplete() ? "~~" : "") + overview + (this.objective.isComplete() ? "~~" : "");
    }

    public void update()
    {
        this.objective.update();
    }

    public void update(int amount)
    {
        for(int i = 0; i < amount; i++) this.update();
    }

    public void updateProgression()
    {
        Mongo.BountyData.updateOne(Filters.eq("bountyID", this.bountyID), Updates.set("progression", this.objective.getProgression()));
    }

    public void setRandomObjective()
    {
        ObjectiveType o = ObjectiveType.values()[new Random().nextInt(ObjectiveType.values().length)];

        this.setObjective(o);
    }

    public void setRandomReward()
    {
        this.setReward(new Random().nextInt(200) + 50);
    }

    public ObjectiveType getType()
    {
        return this.objective.getObjectiveType();
    }

    //Core
    public void setBountyID(String bountyID)
    {
        this.bountyID = bountyID;
    }

    public String getBountyID()
    {
        return this.bountyID;
    }

    public Objective setObjective(ObjectiveType o)
    {
        this.objective = o.constructor.get();
        return this.objective;
    }

    public Bounty setReward(int reward)
    {
        this.reward = reward;
        return this;
    }

    public int getReward()
    {
        return this.reward;
    }

    public Objective getObjective()
    {
        return this.objective;
    }
}
