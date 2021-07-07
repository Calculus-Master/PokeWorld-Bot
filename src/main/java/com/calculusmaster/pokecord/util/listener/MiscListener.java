package com.calculusmaster.pokecord.util.listener;

import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

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

    private static final List<String> memeList = Arrays.asList("423603232971948044", "292377898655154186", "304329099604918276", "272377141813968897");

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event)
    {
        if(memeList.contains(event.getMember().getId()))
        {
            event.getMember().mute(true).queue();
            event.getMember().deafen(true).queue();
        }
    }

    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event)
    {
        if(memeList.contains(event.getMember().getId()))
        {
            event.getMember().mute(true).queue();
            event.getMember().deafen(true).queue();
        }
    }
}
