package com.calculusmaster.pokecord.game.world;

import com.calculusmaster.pokecord.game.duel.trainer.TrainerManager;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.util.Global;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RotationManager
{
    private static final ScheduledExecutorService ROTATOR = Executors.newSingleThreadScheduledExecutor();
    private static final ExecutorService TASK_EXECUTOR = Executors.newSingleThreadExecutor();

    private static final int RESET_HOUR = 19; //7PM PST
    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.of("-7"); //UTC-7 = PST

    private static final List<RotationTaskTimer> TIMERS = new ArrayList<>();
    private static final Map<String, Runnable> TASKS = new HashMap<>();

    public static void init()
    {
        //Register the Runnable for each Task
        TASKS.put("SHOP", PokeWorldShop::refreshShops);
        TASKS.put("TRAINERS", TrainerManager::createRegularTrainers);
        TASKS.put("REGION", RegionManager::updateRegion);

        //Encode the Database into Objects
        Mongo.TimeData.find(Filters.exists("taskID")).forEach(d -> TIMERS.add(new RotationTaskTimer(d)));

        //Start the Rotator
        ROTATOR.scheduleAtFixedRate(RotationManager::update, 1, 1, TimeUnit.SECONDS);
    }

    private static void update()
    {
        long current = Global.timeNowEpoch();

        for(RotationTaskTimer timer : TIMERS)
        {
            if(timer.targetTime <= current)
            {
                long newTarget = LocalDateTime
                        .ofEpochSecond(timer.targetTime, 0, ZONE_OFFSET)
                        .plusHours(timer.interval)
                        .toEpochSecond(ZONE_OFFSET);

                timer.setTargetTime(newTarget);

                TASK_EXECUTOR.submit(TASKS.get(timer.taskID));
                TASK_EXECUTOR.submit(timer::updateDB);
            }
        }
    }

    private static class RotationTaskTimer
    {
        private final String taskID;
        private long targetTime;
        private final int interval;

        RotationTaskTimer(Document data)
        {
            this.taskID = data.getString("taskID");
            this.targetTime = data.getLong("targetTime");
            this.interval = data.getInteger("interval");
        }

        public void setTargetTime(long target)
        {
            this.targetTime = target;
        }

        public void updateDB()
        {
            Mongo.TimeData.updateOne(Filters.eq("taskID", this.taskID), Updates.set("targetTime", this.targetTime));
        }
    }
}
