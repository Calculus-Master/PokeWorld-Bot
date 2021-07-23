package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.game.duel.extension.RaidDuel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RaidEventHelper
{
    private static final Map<String, RaidDuel> SERVER_RAIDS = new HashMap<>();
    private static final Map<String, ScheduledFuture<?>> SCHEDULERS = new HashMap<>();

    public static void start(Guild g, TextChannel channel)
    {
        createRaid(g, channel);

        ScheduledFuture<?> raidEvent = ThreadPoolHandler.RAID.schedule(() -> startRaid(g, channel), 2, TimeUnit.MINUTES);

        SCHEDULERS.put(g.getId(), raidEvent);
    }

    public static void createRaid(Guild g, TextChannel channel)
    {
        RaidDuel raid = RaidDuel.create();
        SERVER_RAIDS.put(g.getId(), raid);

        LoggerHelper.info(RaidEventHelper.class, "Creating new Raid in " + g.getName() + " (" + g.getId() + ")");

        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("A Raid Has Started!");
        embed.setDescription("Join the Raid with `p!raid join`! The battle will begin after a little bit!");

        channel.sendMessageEmbeds(embed.build()).queue();
    }

    public static RaidDuel getRaid(String id)
    {
        return SERVER_RAIDS.get(id);
    }

    public static boolean hasRaid(String id)
    {
        return SERVER_RAIDS.containsKey(id) && SCHEDULERS.containsKey(id) && (!SCHEDULERS.get(id).isCancelled() || !SCHEDULERS.get(id).isDone());
    }

    public static boolean isPlayerReady(String serverID, String playerID)
    {
        return getRaid(serverID).isPlayerWaiting(playerID);
    }

    public static void forceRaid(Guild g, TextChannel channel)
    {
        removeServer(g.getId());

        createRaid(g, channel);
    }

    public static void startRaid(Guild g, TextChannel channel)
    {
        RaidDuel raid = SERVER_RAIDS.get(g.getId());

        if(raid.getWaitingPlayers().isEmpty())
        {
            SERVER_RAIDS.remove(g.getId());
            RaidDuel.delete(raid.getDuelID());
            return;
        }

        channel.sendMessage("Raid Starting! " + raid.getWaitingPlayers().stream().map(s -> "<@" + s + ">").toList()).queue();

        raid.start();

        SpawnEventHelper.removeServer(g.getId());

        LoggerHelper.info(SpawnEventHelper.class, "New Raid Event – " + g.getName() + " (" + g.getId() + ")!");
    }

    public static void removeServer(String serverID)
    {
        SCHEDULERS.get(serverID).cancel(true);
        SCHEDULERS.remove(serverID);
        SERVER_RAIDS.remove(serverID);
    }

    public static void close()
    {
        SERVER_RAIDS.clear();
        SCHEDULERS.values().forEach(future -> future.cancel(true));
        SCHEDULERS.clear();
    }
}
