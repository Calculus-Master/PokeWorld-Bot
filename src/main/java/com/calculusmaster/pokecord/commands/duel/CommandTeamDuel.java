package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.commands.pokemon.CommandTeam;
import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.DuelHelper;
import com.calculusmaster.pokecord.game.TeamDuel;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

/**Temporary, will replace CommandDuel if complete
 * @see com.calculusmaster.pokecord.game.TeamDuel
 */
public class CommandTeamDuel extends Command
{
    public CommandTeamDuel(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    //p!duel @player <size>

    @Override
    public Command runCommand()
    {
        if(this.msg.length != 3 && this.msg.length != 2)
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }
        else if(this.msg.length == 3 && (!isNumeric(2) || this.getInt(2) > CommandTeam.MAX_TEAM_SIZE || this.getInt(2) > this.playerData.getTeam().length()))
        {
            this.embed.setDescription("Error with size. Either it isn't a number, larger than the max of " + CommandTeam.MAX_TEAM_SIZE + ", or larger than your team's size!");
            return this;
        }

        boolean accept = this.msg[1].equals("accept");
        boolean deny = this.msg[1].equals("deny");

        if(DuelHelper.isInDuel(this.player.getId()) && (accept || deny))
        {
            if(accept)
            {
                TeamDuel d = DuelHelper.instance(this.player.getId());

                if(d.getSize() > this.playerData.getTeam().length())
                {
                    this.embed.setDescription("Your team needs to contain at least " + d.getSize() + " to participate! Deleting duel request!");
                    DuelHelper.delete(this.player.getId());
                    return this;
                }

                d.sendTurnEmbed();
                this.embed = null;
            }
            else
            {
                DuelHelper.delete(this.player.getId());
                this.embed.setDescription(this.player.getName() + " denied the duel request!");
            }

            return this;
        }

        int size = 3;

        if(this.msg.length == 3) size = this.getInt(2);

        //Player wants to start a duel with the mention, check all necessary things
        if(DuelHelper.isInDuel(this.player.getId()) && this.msg[1].equals("cancel") && DuelHelper.instance(this.player.getId()).getStatus().equals(DuelHelper.DuelStatus.WAITING))
        {
            DuelHelper.delete(this.player.getId());
            this.embed.setDescription("Duel canceled!");
            return this;
        }
        else if(this.msg[1].equals("cancel")) return this;

        if(DuelHelper.isInDuel(this.player.getId()))
        {
            this.embed.setDescription("You're already in a duel!");
            return this;
        }

        if(this.mentions.size() == 0)
        {
            this.embed.setDescription("You need to mention someone to duel them!");
            return this;
        }

        String opponentID = this.mentions.get(0).getId();

        if(!PlayerDataQuery.isRegistered(opponentID))
        {
            this.embed.setDescription(this.event.getGuild().getMemberById(opponentID).getEffectiveName() + " is not registered! Use p!start <starter> to begin!");
            return this;
        }
        else if(new PlayerDataQuery(opponentID).getTeam() == null)
        {
            this.embed.setDescription(this.mentions.get(0).getEffectiveName() + " needs to create a Pokemon team!");
            return this;
        }
        else if(DuelHelper.isInDuel(opponentID))
        {
            this.embed.setDescription(this.event.getGuild().getMemberById(opponentID).getEffectiveName() + " is already in a Duel!");
            return this;
        }
        else if(this.player.getId().equals(opponentID))
        {
            this.embed.setDescription("You cannot duel yourself!");
            return this;
        }

        TeamDuel d = TeamDuel.create(this.player.getId(), opponentID, size, this.event);

        this.event.getChannel().sendMessage("<@" + opponentID + "> ! " + this.player.getName() + " has challenged you to a duel! Type `p!duel accept` to accept!").queue();
        this.embed = null;
        return this;
    }
}
