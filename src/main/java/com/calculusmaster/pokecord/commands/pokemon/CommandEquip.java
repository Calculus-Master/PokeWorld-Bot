package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.DuelHelper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandEquip extends Command
{
    public CommandEquip(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length != 2 || !this.isNumeric(1) || this.playerData.getZCrystalList() == null)
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }
        else if(this.getInt(1) > this.playerData.getZCrystalList().length() || this.getInt(1) <= 0)
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }
        else if(DuelHelper.isInDuel(this.player.getId()))
        {
            this.event.getChannel().sendMessage(this.playerData.getMention() + ": You can't change a Z-Crystal in the middle of a battle!").queue();
            this.embed = null;
            return this;
        }

        String z = this.playerData.getZCrystalList().getString(this.getInt(1) - 1);

        this.playerData.equipZCrystal(z);

        this.event.getChannel().sendMessage(this.playerData.getMention() + ": Equipped " + z + "!").queue();

        this.embed = null;
        return this;
    }
}
