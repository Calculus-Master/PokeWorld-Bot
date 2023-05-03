package com.calculusmaster.pokecord.util.listener;

import com.calculusmaster.pokecord.game.settings.Settings;
import com.calculusmaster.pokecord.game.settings.core.SingleValue;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.mongo.PlayerData;
import com.calculusmaster.pokecord.mongo.ServerData;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import com.calculusmaster.pokecord.util.helpers.event.SpawnEventHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MiscListener extends ListenerAdapter
{
    @Override
    public void onChannelDelete(ChannelDeleteEvent event)
    {
        if(event.isFromType(ChannelType.TEXT))
        {
            String channelID = event.getChannel().getId();
            ServerData serverData = ServerData.build(event.getGuild().getId());

            //Make sure channel settings don't reference a deleted channel
            for(var s : List.of(Settings.SPAWN_CHANNEL, Settings.DUEL_CHANNEL, Settings.TRADE_CHANNEL))
            {
                var setting = s.getSetting(serverData);
                List<TextChannel> channels = new ArrayList<>(setting.get());

                if(channels.removeIf(t -> t.getId().equals(channelID))) s.update(serverData.getID(), new SingleValue<>(channels));
            }
        }
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event)
    {
        if(PlayerData.isRegistered(event.getMember().getId())) DataHelper.updateServerPlayers(event.getGuild());
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event)
    {
        if(PlayerData.isRegistered(event.getUser().getId())) DataHelper.updateServerPlayers(event.getGuild());
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event)
    {
        Guild server = event.getGuild();

        ServerData.register(server);

        DataHelper.updateServerPlayers(server);
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event)
    {
        String serverID = event.getGuild().getId();

        Mongo.ServerData.deleteOne(Filters.eq("serverID", serverID));

        SpawnEventHelper.removeServer(serverID);
        DataHelper.removeServer(serverID);
    }

    @Override
    public void onUserUpdateName(@NotNull UserUpdateNameEvent event)
    {
        if(PlayerData.isRegistered(event.getUser().getId()))
        {
            Mongo.PlayerData.updateOne(Filters.eq("playerID", event.getUser().getId()), Updates.set("username", event.getNewName()));
        }
    }
}
