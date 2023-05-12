package com.calculusmaster.pokecord.util.listener;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.util.helpers.event.SpawnEventHelper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Listener extends ListenerAdapter
{
    private static Bag<String> SERVER_RECENT_MESSAGES;
    private static final ScheduledExecutorService MESSAGE_UPDATER = Executors.newSingleThreadScheduledExecutor();

    private final MessageEventHandler events; { this.events = new MessageEventHandler(); }

    public static void startSpawnIntervalUpdater()
    {
        SERVER_RECENT_MESSAGES = new HashBag<>();

        MESSAGE_UPDATER.scheduleAtFixedRate(() -> SERVER_RECENT_MESSAGES.forEach(serverID -> {
            SpawnEventHelper.updateSpawnRate(Pokeworld.BOT_JDA.getGuildById(serverID), SERVER_RECENT_MESSAGES.getCount(serverID));
            SERVER_RECENT_MESSAGES.remove(serverID);
        }), 5, 5, TimeUnit.MINUTES);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        //Check if the message was sent by a bot, and skip the listener if true
        if(event.getAuthor().isBot()) return;

        //Ignore private messages
        if(!event.isFromGuild()) return;

        this.events.activateEvent(MessageEventHandler.MessageEvent.REDEEM);
        this.events.activateEvent(MessageEventHandler.MessageEvent.EXPERIENCE);
        this.events.activateEvent(MessageEventHandler.MessageEvent.EGG_EXPERIENCE);

        SERVER_RECENT_MESSAGES.add(event.getGuild().getId());
    }
}
