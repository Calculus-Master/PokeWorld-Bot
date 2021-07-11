package com.calculusmaster.pokecord.util.interfaces;

import com.calculusmaster.pokecord.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface CommandSupplier
{
    Command create(MessageReceivedEvent event, String[] msg);
}