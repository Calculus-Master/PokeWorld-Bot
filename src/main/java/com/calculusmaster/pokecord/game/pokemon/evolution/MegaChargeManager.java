package com.calculusmaster.pokecord.game.pokemon.evolution;

import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MegaChargeManager
{
    private static final ScheduledExecutorService POOL = Executors.newSingleThreadScheduledExecutor();
    private static final ExecutorService UPDATER = Executors.newCachedThreadPool();

    private static final int REGENERATION_TIME = 4; //Hours

    public static void init()
    {
        POOL.scheduleAtFixedRate(MegaChargeManager::regenerateMegaCharges, 0, 1, TimeUnit.HOURS);
    }

    public static void addChargeEvent(String UUID, boolean isMega)
    {
        Document data = new Document()
                .append("UUID", UUID)
                .append("time", REGENERATION_TIME)
                .append("blocked", isMega);

        Mongo.MegaChargeData.insertOne(data);

        LoggerHelper.info(MegaChargeManager.class, "Queueing Mega Charge Regeneration for " + UUID + ".");
    }

    public static void blockCharging(String UUID)
    {
        Mongo.MegaChargeData.updateMany(Filters.eq("UUID", UUID), Updates.set("blocked", true));
    }

    public static void removeBlocking(String UUID)
    {
        Mongo.MegaChargeData.updateMany(Filters.eq("UUID", UUID), Updates.set("blocked", false));
    }

    public static void regenerateMegaCharges()
    {
        //Reduce all timers by 1 (if charging has not been blocked)
        Mongo.MegaChargeData.updateMany(Filters.and(Filters.ne("blocked", true), Filters.gt("time", 0)), Updates.inc("time", -1));

        //Find all timers that have reached 0
        List<Document> completedChargeEvents = Mongo.MegaChargeData.find(Filters.eq("time", 0)).into(new ArrayList<>());
        Mongo.MegaChargeData.deleteMany(Filters.eq("time", 0));

        for(Document data : completedChargeEvents)
        {
            UPDATER.execute(() -> {
                Pokemon p = Pokemon.build(data.getString("UUID"));

                p.regenerateMegaCharge();
                p.updateMegaCharges();

                LoggerHelper.info(MegaChargeManager.class, "Regenerated a Mega Charge for " + p.getName() + "(" + p.getUUID() + ").");
            });
        }
    }
}
