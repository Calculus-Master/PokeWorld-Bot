package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

public class CommandDuel extends Command
{
    public CommandDuel(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "duel <player:accept>");
    }

    @Override
    public Command runCommand() throws IOException
    {
        if(this.msg.length != 2)
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }
        else if(Duel.isInDuel(this.player.getId()) && (this.msg[1].toLowerCase().equals("accept") || this.msg[1].toLowerCase().equals("deny")))
        {
            boolean accept = this.msg[1].toLowerCase().equals("accept");
            if(accept)
            {
                Duel d = Duel.getInstance(this.player.getId());
                d.start();
                d.sendInitialTurnEmbed(this.event);
                this.embed = null;
            }
            else
            {
                Duel.remove(this.player.getId());
                this.embed.setDescription(this.player.getName() + " denied the duel!");
            }

            //Duel.printAllDuels();
            return this;
        }
        else if(Duel.isInDuel(this.player.getId()))
        {
            this.embed.setDescription("You are already in a Duel!");
            return this;
        }

        String opponentID = this.msg[1].substring("<@!".length(), this.msg[1].lastIndexOf(">"));

        if(!PlayerDataQuery.isRegistered(opponentID))
        {
            this.embed.setDescription(this.event.getGuild().getMemberById(opponentID).getEffectiveName() + " is not registered! Use p!start <starter> to begin!");
            return this;
        }
        else if(Duel.isInDuel(opponentID))
        {
            this.embed.setDescription(this.event.getGuild().getMemberById(opponentID).getEffectiveName() + " is already in a Duel!");
            return this;
        }
        else if(this.player.getId().equals(opponentID))
        {
            this.embed.setDescription("You cannot duel yourself!");
            return this;
        }
        else
        {
            Duel d = Duel.initiate(this.player.getId(), opponentID);

            this.event.getChannel().sendMessage(d.getRequestEmbed().build()).queue();
            this.embed = null;
            d.setDuelImage();
            return this;
        }
    }
}
