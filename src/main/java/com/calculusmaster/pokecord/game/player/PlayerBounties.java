package com.calculusmaster.pokecord.game.player;

import com.calculusmaster.pokecord.game.objectives.Bounty;
import com.calculusmaster.pokecord.game.objectives.ObjectiveType;
import com.calculusmaster.pokecord.game.objectives.types.AbstractObjective;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

public class PlayerBounties
{
    private static final ExecutorService UPDATER = Executors.newFixedThreadPool(5);
    public static final int MAX_BOUNTIES = 6;

    private final PlayerDataQuery playerData;
    private final List<Bounty> bounties;
    private final Map<ObjectiveType, Integer> objectiveTypes;

    public PlayerBounties(PlayerDataQuery playerData)
    {
        this.playerData = playerData;
        this.bounties = new ArrayList<>();
        this.objectiveTypes = new HashMap<>();
    }

    public PlayerBounties(PlayerDataQuery playerData, List<Document> data)
    {
        this(playerData);

        data.forEach(d -> {
            Bounty bounty = new Bounty(d);
            this.bounties.add(bounty);
            bounty.getObjectives().forEach(o -> this.objectiveTypes.put(o.getObjectiveType(), this.objectiveTypes.getOrDefault(o.getObjectiveType(), 0) + 1));
        });
    }

    public List<Document> serialize()
    {
        return this.bounties.stream().map(Bounty::serialize).toList();
    }

    public boolean hasObjective(ObjectiveType type)
    {
        return this.objectiveTypes.containsKey(type);
    }

    public void add(Bounty b)
    {
        this.bounties.add(b);

        UPDATER.submit(() -> Mongo.PlayerData.updateOne(this.playerData.getQuery(), Updates.push("bounties", b.serialize())));
    }

    public void remove(int number)
    {
        this.bounties.remove(number);

        UPDATER.submit(() -> Mongo.PlayerData.updateOne(this.playerData.getQuery(), Updates.set("bounties", this.serialize())));
    }

    public void updateBounty(int number)
    {
        UPDATER.submit(() -> Mongo.PlayerData.updateOne(this.playerData.getQuery(), Updates.set("bounties." + number, this.bounties.get(number).serialize())));
    }

    public void checkAndUpdateObjectives(ObjectiveType type, Predicate<AbstractObjective> checker, int amount)
    {
        for(int i = 0; i < this.bounties.size(); i++)
        {
            Bounty bounty = this.bounties.get(i);
            for(int k = 0; k < bounty.getObjectives().size(); k++)
            {
                AbstractObjective objective = bounty.getObjectives().get(k);

                if(objective.getObjectiveType().equals(type) && !objective.isComplete() && checker.test(objective))
                {
                    objective.setProgress(objective.getProgress() + amount);

                    final int bountyIndex = i;
                    final int objectiveIndex = k;

                    UPDATER.submit(() -> {
                        Mongo.PlayerData.updateOne(this.playerData.getQuery(), Updates.inc("bounties." + bountyIndex + ".objectives." + objectiveIndex + ".progress", amount));

                        if(bounty.isComplete()) playerData.directMessage("You have completed a bounty.");
                    });
                }
            }
        }
    }

    public List<Bounty> getBounties()
    {
        return this.bounties;
    }
}
