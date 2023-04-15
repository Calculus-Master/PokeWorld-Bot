package com.calculusmaster.pokecord.game.player;

import com.calculusmaster.pokecord.game.objectives.ObjectiveType;
import com.calculusmaster.pokecord.game.objectives.ResearchTask;
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

public class PlayerResearchTasks
{
    private static final ExecutorService UPDATER = Executors.newFixedThreadPool(5);
    public static int MAX_TASKS = 6;

    private final PlayerDataQuery playerData;
    private final List<ResearchTask> researchTasks;
    private final Map<ObjectiveType, Integer> objectiveTypes;

    public PlayerResearchTasks(PlayerDataQuery playerData)
    {
        this.playerData = playerData;
        this.researchTasks = new ArrayList<>();
        this.objectiveTypes = new HashMap<>();
    }

    public PlayerResearchTasks(PlayerDataQuery playerData, List<Document> data)
    {
        this(playerData);

        data.forEach(d -> {
            ResearchTask researchTask = new ResearchTask(d);
            this.researchTasks.add(researchTask);
            researchTask.getObjectives().forEach(o -> this.objectiveTypes.put(o.getObjectiveType(), this.objectiveTypes.getOrDefault(o.getObjectiveType(), 0) + 1));
        });
    }

    public List<Document> serialize()
    {
        return this.researchTasks.stream().map(ResearchTask::serialize).toList();
    }

    public boolean hasObjective(ObjectiveType type)
    {
        return this.objectiveTypes.containsKey(type);
    }

    public void add(ResearchTask b)
    {
        this.researchTasks.add(b);

        UPDATER.submit(() -> Mongo.PlayerData.updateOne(this.playerData.getQuery(), Updates.push("tasks", b.serialize())));
    }

    public void remove(int number)
    {
        this.researchTasks.remove(number);

        UPDATER.submit(() -> Mongo.PlayerData.updateOne(this.playerData.getQuery(), Updates.set("tasks", this.serialize())));
    }

    public void updateTask(int number)
    {
        UPDATER.submit(() -> Mongo.PlayerData.updateOne(this.playerData.getQuery(), Updates.set("tasks." + number, this.researchTasks.get(number).serialize())));
    }

    public void checkAndUpdateObjectives(ObjectiveType type, Predicate<AbstractObjective> checker, int amount)
    {
        for(int i = 0; i < this.researchTasks.size(); i++)
        {
            ResearchTask researchTask = this.researchTasks.get(i);
            for(int k = 0; k < researchTask.getObjectives().size(); k++)
            {
                AbstractObjective objective = researchTask.getObjectives().get(k);

                if(objective.getObjectiveType().equals(type) && !objective.isComplete() && checker.test(objective))
                {
                    objective.setProgress(objective.getProgress() + amount);

                    final int taskIndex = i;
                    final int objectiveIndex = k;

                    UPDATER.submit(() -> {
                        Mongo.PlayerData.updateOne(this.playerData.getQuery(), Updates.inc("tasks." + taskIndex + ".objectives." + objectiveIndex + ".progress", amount));

                        if(researchTask.isComplete()) playerData.directMessage("You have completed a Research Task.");
                    });
                }
            }
        }
    }

    public List<ResearchTask> getResearchTasks()
    {
        return this.researchTasks;
    }
}
