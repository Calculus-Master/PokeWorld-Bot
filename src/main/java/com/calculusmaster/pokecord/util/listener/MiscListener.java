package com.calculusmaster.pokecord.util.listener;

import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
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
}
