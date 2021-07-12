package com.calculusmaster.pokecord.game.bounties;

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
        this.objective = switch(o) {
            case DEFEAT_POKEMON -> new DefeatGenericObjective();
            case DEFEAT_POKEMON_TYPE -> new DefeatTypeObjective();
            case DEFEAT_LEGENDARY -> new DefeatLegendaryObjective();
            case BUY_ITEMS -> new BuyItemsObjective();
            case USE_ZMOVE -> new UseZMoveObjective();
            case USE_MAX_MOVE -> new UseMaxMoveObjective();
            case COMPLETE_TRADE -> new CompleteTradeObjective();
            case COMPLETE_PVP_DUEL -> new CompletePVPDuelObjective();
            case COMPLETE_WILD_DUEL -> new CompleteWildDuelObjective();
            case COMPLETE_ELITE_DUEL -> new CompleteEliteDuelObjective();
            case WIN_PVP_DUEL -> new WinPVPDuelObjective();
            case WIN_WILD_DUEL -> new WinWildDuelObjective();
            case WIN_ELITE_DUEL -> new WinEliteDuelObjective();
            case CATCH_POKEMON -> new CatchGenericObjective();
            case CATCH_POKEMON_TYPE -> new CatchTypeObjective();
            case CATCH_POKEMON_NAME -> new CatchNameObjective();
            case EARN_XP_POKEPASS -> new EarnPokePassXPObjective();
            case EARN_XP_POKEMON -> new EarnPokemonXPObjective();
        };

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

    @Override
    public String toString() {
        return "Bounty{" +
                "bountyID='" + bountyID + '\'' +
                ", objective=" + objective +
                ", reward=" + reward +
                '}';
    }
}
