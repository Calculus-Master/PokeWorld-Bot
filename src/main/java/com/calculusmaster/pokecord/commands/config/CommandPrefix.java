package com.calculusmaster.pokecord.commands.config;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandPrefix extends Command
{
    public CommandPrefix(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length != 2)
        {
            this.embed.setDescription(CommandInvalid.getShort());
        }
        else
        {
            this.embed.setDescription("Prefix has been changed to `" + this.msg[1] + "`!");
            this.serverData.setPrefix(this.msg[1]);
        }
        return this;
    }
}
