package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandTrade extends Command
{
    public CommandTrade(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 1)
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        if(this.msg.length == 2)
        {
            if(this.msg[1].equals("accept"))
            {

            }
            else if(this.msg[1].equals("deny"))
            {

            }
            else if(this.msg[1].contains("<!@"))
            {

            }
            else
            {
                this.embed.setDescription(CommandInvalid.getShort());
                return this;
            }
        }
        else if(this.msg.length == 4)
        {
            boolean hasAddRemove = this.msg[2].equals("add") || this.msg[2].equals("remove");
            if(this.msg[1].equals("credits"))
            {

            }
            else if(this.msg[1].equals("pokemon"))
            {

            }
        }
    }
}
