package com.calculusmaster.pokecord.game.pokemon.evolution;

import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.util.cacheold.PokemonDataCache;
import com.calculusmaster.pokecord.util.helpers.IDHelper;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MegaChargeManager
{
    private static final ScheduledExecutorService POOL = Executors.newSingleThreadScheduledExecutor();
    private static final ExecutorService UPDATER = Executors.newCachedThreadPool();

    private static final Map<String, List<MegaChargeEvent>> MEGA_CHARGE_EVENTS = Collections.synchronizedMap(new HashMap<>());

    private static final int REGENERATION_TIME = 3 * 3600; //Seconds

    public static void init()
    {
        Mongo.MegaChargeData.find().forEach(d -> {
            MegaChargeEvent e = new MegaChargeEvent(d);

            if(!MEGA_CHARGE_EVENTS.containsKey(e.getUUID())) MEGA_CHARGE_EVENTS.put(e.getUUID(), new ArrayList<>());

            MEGA_CHARGE_EVENTS.get(e.getUUID()).add(e);
        });

        POOL.scheduleAtFixedRate(MegaChargeManager::refreshMegaCharges, 3, 1, TimeUnit.SECONDS);
    }

    public static void refreshMegaCharges()
    {
        for(Map.Entry<String, List<MegaChargeEvent>> e : MEGA_CHARGE_EVENTS.entrySet())
            for(MegaChargeEvent event : e.getValue())
                if(!event.isBlocked())
                {
                    event.decr();

                    if(event.isComplete())
                    {
                        //Remove from Map
                        MEGA_CHARGE_EVENTS.get(event.getUUID()).remove(event);
                        if(MEGA_CHARGE_EVENTS.get(event.getUUID()).isEmpty()) MEGA_CHARGE_EVENTS.remove(event.getUUID());

                        UPDATER.submit(() -> MegaChargeManager.regenerate(event));
                    }
                    else UPDATER.submit(() -> Mongo.MegaChargeData.updateOne(Filters.eq("chargeID", event.getChargeID()), Updates.set("time", event.getTime())));
                }
    }

    private static void regenerate(MegaChargeEvent event)
    {
        //Update Pokemon
        PokemonEntity e = PokemonEntity.valueOf(
                Objects.requireNonNull(Mongo.PokemonData.findOneAndUpdate(Filters.eq("UUID", event.getUUID()), Updates.inc("megacharges", 1)))
                        .getString("entity")
        );

        PokemonDataCache.updateCache(event.getUUID());

        //Remove from DB
        Mongo.MegaChargeData.deleteOne(Filters.eq("chargeID", event.getChargeID()));

        LoggerHelper.info(MegaChargeManager.class, "Regenerated a Mega Charge for " + event.getUUID() + " (Entity: " + e.getName() + ", Charge ID: " + event.getChargeID() + ").");
    }

    public static List<MegaChargeEvent> getEvents(String UUID)
    {
        return MEGA_CHARGE_EVENTS.getOrDefault(UUID, new ArrayList<>());
    }

    public static void addChargeEvent(String UUID, boolean isMega)
    {
        MegaChargeEvent event = new MegaChargeEvent(UUID, REGENERATION_TIME, isMega);

        if(!MEGA_CHARGE_EVENTS.containsKey(UUID)) MEGA_CHARGE_EVENTS.put(UUID, new ArrayList<>());
        MEGA_CHARGE_EVENTS.get(UUID).add(event);

        UPDATER.submit(() -> Mongo.MegaChargeData.insertOne(event.serialize()));

        LoggerHelper.info(MegaChargeManager.class, "Queueing Mega Charge Regeneration for " + UUID + ".");
    }

    public static void setBlocked(String UUID)
    {
        if(MEGA_CHARGE_EVENTS.containsKey(UUID))
            for(MegaChargeEvent e : MEGA_CHARGE_EVENTS.get(UUID)) e.setBlocked(true);

        UPDATER.submit(() -> Mongo.MegaChargeData.updateMany(Filters.eq("UUID", UUID), Updates.set("blocked", true)));
    }

    public static void removeBlocked(String UUID)
    {
        if(MEGA_CHARGE_EVENTS.containsKey(UUID))
            for(MegaChargeEvent e : MEGA_CHARGE_EVENTS.get(UUID)) e.setBlocked(false);

        UPDATER.submit(() -> Mongo.MegaChargeData.updateMany(Filters.eq("UUID", UUID), Updates.set("blocked", false)));
    }

    public static class MegaChargeEvent
    {
        private final String chargeID;
        private final String UUID;
        private int time;
        private boolean blocked;

        MegaChargeEvent(String UUID, int time, boolean blocked)
        {
            this.chargeID = IDHelper.alphanumeric(8);
            this.UUID = UUID;
            this.time = time;
            this.blocked = blocked;
        }

        MegaChargeEvent(Document data)
        {
            this.chargeID = data.getString("chargeID");
            this.UUID = data.getString("UUID");
            this.time = data.getInteger("time");
            this.blocked = data.getBoolean("blocked");
        }

        public Document serialize()
        {
            return new Document()
                    .append("chargeID", this.chargeID)
                    .append("UUID", this.UUID)
                    .append("time", REGENERATION_TIME)
                    .append("blocked", this.blocked);
        }

        public boolean isBlocked()
        {
            return this.blocked;
        }

        public void setBlocked(boolean blocked)
        {
            this.blocked = blocked;
        }

        public String getUUID()
        {
            return this.UUID;
        }

        public String getChargeID()
        {
            return this.chargeID;
        }

        public void decr()
        {
            this.time--;
        }

        public boolean isComplete()
        {
            return this.time <= 0;
        }

        public int getTime()
        {
            return this.time;
        }
    }
}
