package com.calculusmaster.pokecord.util.listener;

import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.commands.Commands;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Listener extends ListenerAdapter
{
    private static final ExecutorService EVENT_THREAD_POOL = Executors.newFixedThreadPool(3);

    private final Map<String, Long> cooldowns = new HashMap<>();
    int cooldown = 1; //Seconds

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
        Random r = new Random(System.currentTimeMillis());

        //If bot is mentioned, send the server prefix
        if(event.getMessage().getMentionedMembers().stream().anyMatch(m -> m.getId().equals("718169293904281610"))) event.getChannel().sendMessage("<@" + player.getId() + ">: My prefix is `" + serverQuery.getPrefix() + "`!").queue();

        //If bot commands are disabled in this channel, skip the listener
        if(!serverQuery.getBotChannels().isEmpty() && !serverQuery.getBotChannels().contains(event.getChannel().getId()) && !Global.userHasAdmin(server, player))
        {
            Pokecord.BOT_JDA.openPrivateChannelById(player.getId()).flatMap(channel -> channel.sendMessage("Bot Commands are not allowed in that channel!")).queue();
            return;
        }

        //If the message starts with the right prefix, continue, otherwise skip the listener
        if(msg[0].startsWith(serverQuery.getPrefix()))
        {
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

            Commands.execute(msg[0], event, msg);

            if(r.nextInt(5000) < 1) EVENT_THREAD_POOL.execute(() -> redeemEvent(event));
        }

        if(r.nextInt(10) <= 3) EVENT_THREAD_POOL.execute(() -> expEvent(event));
    }

    private static void redeemEvent(MessageReceivedEvent event)
    {
        PlayerDataQuery p = new PlayerDataQuery(event.getAuthor().getId());
        p.changeRedeems(1);

        p.getStats().incr(PlayerStatistic.NATURAL_REDEEMS_EARNED);

        event.getChannel().sendMessage(p.getMention() + ": You earned a Redeem!").queue();
    }

    private static void expEvent(MessageReceivedEvent event)
    {
        PlayerDataQuery data = new PlayerDataQuery(event.getAuthor().getId());
        Pokemon p = data.getSelectedPokemon();

        int initL = p.getLevel();

        int experience = (int)(new Random().nextInt(200) * (1 + Math.random()));

        p.addExp(experience);

        EVENT_THREAD_POOL.execute(() -> data.updateBountyProgression(b -> {
            if(b.getType().equals(ObjectiveType.EARN_XP_POKEMON)) b.update(experience);
        }));

        if(p.getLevel() != initL)
        {
            event.getChannel().sendMessage(data.getMention() + ": Your " + p.getName() + " is now Level " + p.getLevel() + "!").queue();
        }

        Pokemon.updateExperience(p);
    }
}
