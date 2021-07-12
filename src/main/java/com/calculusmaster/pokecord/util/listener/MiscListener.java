package com.calculusmaster.pokecord.util.listener;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import com.calculusmaster.pokecord.util.helpers.SpawnEventHelper;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MiscListener extends ListenerAdapter
{
    @Override
    public void onTextChannelDelete(@NotNull TextChannelDeleteEvent event)
    {
        String channelID = event.getChannel().getId();
        ServerDataQuery serverData = new ServerDataQuery(event.getGuild().getId());

        //Make sure spawn channel doesn't reference a deleted channel
        if(serverData.getSpawnChannels().contains(channelID))
        {
            serverData.removeSpawnChannel(channelID);
        }
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event)
    {
        if(PlayerDataQuery.isRegistered(event.getMember().getId())) DataHelper.updateServerPlayers(event.getGuild());
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event)
    {
        if(PlayerDataQuery.isRegistered(event.getUser().getId())) DataHelper.updateServerPlayers(event.getGuild());
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event)
    {
        Guild server = event.getGuild();

        if(!ServerDataQuery.isRegistered(server)) ServerDataQuery.register(server);

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
}
