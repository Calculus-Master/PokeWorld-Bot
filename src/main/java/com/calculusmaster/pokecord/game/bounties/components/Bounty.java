package com.calculusmaster.pokecord.game.bounties.components;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.*;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.helpers.IDHelper;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.Random;

public class Bounty
{
    public static int MAX_BOUNTIES_HELD;
    public static int POKEPASS_EXP_YIELD;
    public static int BOUNTY_REWARD_MAX;
    public static int BOUNTY_REWARD_MIN;

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

        if(b.objective instanceof AbstractTypeObjective o) o.setType(d.getString("type"));
        if(b.objective instanceof AbstractNameObjective o) o.setName(d.getString("name"));
        if(b.objective instanceof AbstractStatObjective o) o.setStat(d.getString("stat"));
        if(b.objective instanceof AbstractPoolObjective o) o.setPool(d.getList("pool", String.class));
        if(b.objective instanceof AbstractPowerObjective o) o.setPower(d.getInteger("power"));
        if(b.objective instanceof AbstractAccuracyObjective o) o.setAccuracy(d.getInteger("accuracy"));
        if(b.objective instanceof AbstractCategoryObjective o) o.setCategory(d.getString("category"));

        return b;
    }

    public void upload()
    {
        Document data = new Document()
                .append("bountyID", this.getBountyID())
                .append("reward", this.getReward());

        this.objective.addObjectiveData(data);

        LoggerHelper.logDatabaseInsert(Bounty.class, data);

        Mongo.BountyData.insertOne(data);
    }

    public void delete()
    {
        Mongo.BountyData.deleteOne(Filters.eq("bountyID", this.bountyID));
    }

    public static void delete(String ID)
    {
        Mongo.BountyData.deleteOne(Filters.eq("bountyID", ID));
    }

    public void setElite()
    {
        this.setReward((int)(this.getReward() * ((Math.random() + 1) * 15)));
        this.objective.setTarget((Math.random() + 1) * 5);
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

    public void updateIf(boolean update)
    {
        this.updateIf(update, 1);
    }

    public void updateIf(boolean update, int amount)
    {
        if(update) this.update(amount);
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
        this.setReward(new Random().nextInt(BOUNTY_REWARD_MAX - BOUNTY_REWARD_MIN + 1) + BOUNTY_REWARD_MIN);
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
