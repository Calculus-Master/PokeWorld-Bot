package com.calculusmaster.pokecord.util.listener;

import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.commands.Commands;
import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import com.calculusmaster.pokecord.util.custom.ExtendedIntegerMap;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.calculusmaster.pokecord.util.helpers.ThreadPoolHandler;
import com.calculusmaster.pokecord.util.helpers.event.SpawnEventHelper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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
        SERVER_RECENT_MESSAGES = new ExtendedIntegerMap<String>().withDefaultKeys(Pokecord.BOT_JDA.getGuilds().stream().map(ISnowflake::getId).collect(Collectors.toList()));

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            SERVER_RECENT_MESSAGES.forEach((server, messages) -> SpawnEventHelper.updateSpawnRate(Pokecord.BOT_JDA.getGuildById(server), messages));
            SERVER_RECENT_MESSAGES.reset();
        }, 5, 5, TimeUnit.MINUTES);
    }

    private static ExtendedIntegerMap<String> SERVER_RECENT_MESSAGES;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        //Check if the message was sent by a bot, and skip the listener if true
        if(event.getAuthor().isBot()) return;

        //If a link is sent, skip the listener
        if(event.getMessage().getContentRaw().toLowerCase().startsWith("http")) return;

        //If any attachment is sent, skip the listener
        if(event.getMessage().getAttachments().size() > 0) return;

        User player = event.getAuthor();
        Guild server = event.getGuild();
        String[] msg = event.getMessage().getContentRaw().toLowerCase().trim().split("\\s+");
        ServerDataQuery serverQuery = new ServerDataQuery(server.getId());

        //If bot is mentioned, send the server prefix
        if(event.getMessage().getMentions().getMembers().stream().anyMatch(m -> m.getId().equals("718169293904281610")) && serverQuery.isAbleToSendMessages(event.getChannel().getId())) event.getChannel().sendMessage("<@" + player.getId() + ">: My prefix is `" + serverQuery.getPrefix() + "`!").queue();

        //Update the Event Handler's Stored Event
        this.events.updateEvent(event);

        //If the message starts with the right prefix, continue, otherwise skip the listener
        if(msg[0].startsWith(serverQuery.getPrefix()))
        {
            //If bot commands are disabled in this channel, skip the listener
            if(!serverQuery.getBotChannels().isEmpty() && !serverQuery.getBotChannels().contains(event.getChannel().getId()))
            {
                if(!PLAYERS_NOTIFIED_BOT_CHANNELS.contains(player.getId()))
                {
                    Pokecord.BOT_JDA.openPrivateChannelById(player.getId()).flatMap(channel -> channel.sendMessage("Bot Commands are not allowed in that channel!")).queue();
                    PLAYERS_NOTIFIED_BOT_CHANNELS.add(player.getId());
                }

                return;
            }

            //Check cooldown
            if(this.cooldowns.containsKey(player.getId()) && !msg[0].contains("catch") && !msg[0].contains("use"))
            {
                if(System.currentTimeMillis() - this.cooldowns.get(player.getId()) <= this.cooldown * 1000L)
                {
                    event.getMessage().getChannel().sendMessage("<@" + player.getId() + ">: You're sending commands too quickly!").queue();
                    return;
                }
                else this.cooldowns.put(player.getId(), System.currentTimeMillis());
            }
            else this.cooldowns.put(player.getId(), System.currentTimeMillis());

            LoggerHelper.info(Listener.class, "Parsing: " + Arrays.toString(msg));

            //Remove prefix from the message array, msg[0] is the raw command name
            msg[0] = msg[0].substring(serverQuery.getPrefix().length());

            try
            {
                if(Commands.COMMAND_THREAD_POOL) ThreadPoolHandler.LISTENER_COMMAND.execute(() -> Commands.execute(msg[0], event, msg));
                else Commands.execute(msg[0], event, msg);
            }
            catch (Exception e)
            {
                LoggerHelper.reportError(Listener.class, "Command Execution Failed! (" + Arrays.toString(msg) + ")", e);
                event.getChannel().sendMessage("<@" + player.getId() + ">: An error has occurred while using this command. Please report it with `p!report`!").queue();
            }
        }

        this.events.activateEvent(MessageEventHandler.MessageEvent.REDEEM);
        this.events.activateEvent(MessageEventHandler.MessageEvent.EXPERIENCE);
        this.events.activateEvent(MessageEventHandler.MessageEvent.EGG_EXPERIENCE);

        SERVER_RECENT_MESSAGES.increase(server.getId());
    }
}
