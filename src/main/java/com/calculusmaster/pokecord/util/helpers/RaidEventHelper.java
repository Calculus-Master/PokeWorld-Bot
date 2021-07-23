package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.game.duel.extension.RaidDuel;
import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RaidEventHelper
{
    private static final Map<String, RaidDuel> SERVER_RAIDS = new HashMap<>();
    private static final Map<String, ScheduledFuture<?>> SCHEDULERS = new HashMap<>();

    public static void start(Guild g)
    {
        ScheduledFuture<?> raidEvent = ThreadPoolHandler.RAID.schedule(() -> startRaid(g), 2, TimeUnit.MINUTES);

        SCHEDULERS.put(g.getId(), raidEvent);

        createRaid(g);
    }

    public static void createRaid(Guild g)
    {
        RaidDuel raid = RaidDuel.create();
        SERVER_RAIDS.put(g.getId(), raid);
    }

    public static RaidDuel getRaid(String id)
    {
        return SERVER_RAIDS.get(id);
    }

    public static boolean hasRaid(String id)
    {
        return SERVER_RAIDS.containsKey(id);
    }

    public static boolean isPlayerReady(String serverID, String playerID)
    {
        return getRaid(serverID).isPlayerWaiting(playerID);
    }

    public static void forceRaid(Guild g)
    {
        removeServer(g.getId());

        createRaid(g);
    }

    public static void startRaid(Guild g)
    {
        if(SERVER_RAIDS.get(g.getId()).getWaitingPlayers().isEmpty()) return;

        List<TextChannel> channels = new ServerDataQuery(g.getId()).getSpawnChannels().stream().map(g::getTextChannelById).filter(Objects::nonNull).collect(Collectors.toList());

        if(channels.isEmpty())
        {
            LoggerHelper.warn(SpawnEventHelper.class, g.getName() + " has no Spawn Channels! Skipping raid event...");
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("A Raid Has Started!");
        embed.setDescription("Join the Raid with `p!raid join`! The battle will begin after a little bit!");

        channels.get(0).sendMessageEmbeds(embed.build()).queue();

        SERVER_RAIDS.get(g.getId()).start();

        SpawnEventHelper.removeServer(g.getId());

        LoggerHelper.info(SpawnEventHelper.class, "New Raid Event – " + g.getName() + " (" + g.getId() + ")!");
    }

    public static void removeServer(String serverID)
    {
        SCHEDULERS.get(serverID).cancel(true);
        SCHEDULERS.remove(serverID);
        SERVER_RAIDS.remove(serverID);
    }
}
