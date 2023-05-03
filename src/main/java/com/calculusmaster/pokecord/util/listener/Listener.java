package com.calculusmaster.pokecord.util.listener;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.util.custom.ExtendedIntegerMap;
import com.calculusmaster.pokecord.util.helpers.event.SpawnEventHelper;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Listener extends ListenerAdapter
{
    private final Map<String, Long> cooldowns = new HashMap<>();
    int cooldown = 1; //Seconds

    private final MessageEventHandler events; { this.events = new MessageEventHandler(); }

    private static final List<String> PLAYERS_NOTIFIED_BOT_CHANNELS = new ArrayList<>();

    public static void startSpawnIntervalUpdater()
    {
        SERVER_RECENT_MESSAGES = new ExtendedIntegerMap<String>().withDefaultKeys(Pokeworld.BOT_JDA.getGuilds().stream().map(ISnowflake::getId).collect(Collectors.toList()));

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            SERVER_RECENT_MESSAGES.forEach((server, messages) -> SpawnEventHelper.updateSpawnRate(Pokeworld.BOT_JDA.getGuildById(server), messages));
            SERVER_RECENT_MESSAGES.reset();
        }, 5, 5, TimeUnit.MINUTES);
    }

    private static ExtendedIntegerMap<String> SERVER_RECENT_MESSAGES;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        //Check if the message was sent by a bot, and skip the listener if true
        if(event.getAuthor().isBot()) return;

        this.events.activateEvent(MessageEventHandler.MessageEvent.REDEEM);
        this.events.activateEvent(MessageEventHandler.MessageEvent.EXPERIENCE);
        this.events.activateEvent(MessageEventHandler.MessageEvent.EGG_EXPERIENCE);

        SERVER_RECENT_MESSAGES.increase(event.getGuild().getId());
    }
}
