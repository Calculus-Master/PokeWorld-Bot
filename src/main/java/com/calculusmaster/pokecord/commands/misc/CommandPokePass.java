package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandPokePass extends Command
{
    public CommandPokePass(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        return this;
    }
}
