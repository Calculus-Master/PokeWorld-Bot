package com.calculusmaster.pokecord.commands.moves;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

public class CommandAbilityInfo extends Command
{
    private static final Map<String, String> ABILITY_INFO = new HashMap<>();

    public static void init()
    {

    }

    public CommandAbilityInfo(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length >= 2 && ABILITY_INFO.containsKey(this.getAbility()))
        {
            this.embed.setTitle("Ability Info: " + this.getAbility());
            this.embed.setDescription(ABILITY_INFO.get(this.getAbility()));

        }
        else this.embed.setDescription(CommandInvalid.getShort());
        return this;
    }

    private String getAbility()
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < this.msg.length; i++) sb.append(this.msg[i]).append(" ");
        return Global.normalCase(sb.toString().trim());
    }
}
