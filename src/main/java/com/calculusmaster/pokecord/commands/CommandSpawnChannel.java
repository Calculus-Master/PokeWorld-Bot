package com.calculusmaster.pokecord.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandSpawnChannel extends Command
{
    public CommandSpawnChannel(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "setspawnchannel");
    }

    @Override
    public Command runCommand()
    {
        this.serverData.setSpawnChannel(this.event.getChannel());
        this.embed.setDescription("Spawns are now set to #" + this.event.getChannel().getName());
        return this;
    }
}
