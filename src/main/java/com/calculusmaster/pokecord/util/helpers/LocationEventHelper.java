package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.game.enums.elements.Location;
import com.calculusmaster.pokecord.game.enums.elements.Time;
import net.dv8tion.jda.api.entities.Guild;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LocationEventHelper
{
    private static final Map<String, Location> SERVER_LOCATIONS = new HashMap<>();
    private static final Map<String, ScheduledFuture<?>> SCHEDULERS = new HashMap<>();

    public static void start(Guild g)
    {
        start(g, 10);
    }

    public static void start(Guild g, int delay)
    {
        ScheduledFuture<?> locationEvent = ThreadPoolHandler.LOCATION.scheduleWithFixedDelay(() -> changeLocation(g), delay, 2, TimeUnit.HOURS);

        SCHEDULERS.put(g.getId(), locationEvent);
    }

    public static void forceLocation(Guild g, Location loc)
    {
        removeServer(g.getId());

        changeLocation(g, loc);

        start(g);
    }

    public static Location getLocation(String serverID)
    {
        return SERVER_LOCATIONS.get(serverID);
    }

    public static Time getTime()
    {
        int hour = LocalDateTime.now(ZoneId.of("America/Los_Angeles")).getHour();

        if(hour == 17 || hour == 18) return Time.DUSK;
        else if(hour <= 5 || hour >= 19) return Time.NIGHT;
        else return Time.DAY;
    }

    private static void changeLocation(Guild g)
    {
        changeLocation(g, Location.values()[new Random().nextInt(Location.values().length)]);
    }

    private static void changeLocation(Guild g, Location location)
    {
        LoggerHelper.info(LocationEventHelper.class, "New Location Event – " + g.getName() + " (" + g.getId() + ") - Location: " + location.toString() + " | Region: " + location.region + " | Time: " + getTime().toString());
        SERVER_LOCATIONS.put(g.getId(), location);
    }

    public static void removeServer(String serverID)
    {
        SCHEDULERS.get(serverID).cancel(true);
        SCHEDULERS.remove(serverID);
        SERVER_LOCATIONS.remove(serverID);
    }
}
