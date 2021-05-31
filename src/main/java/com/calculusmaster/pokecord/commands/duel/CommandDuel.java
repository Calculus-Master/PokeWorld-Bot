package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.commands.pokemon.CommandTeam;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.PokemonRarity;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

/**Temporary, will replace CommandDuel if complete
 * @see Duel
 */
public class CommandDuel extends Command
{
    public CommandDuel(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    //p!duel @player <size>

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 1)
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }
        else if(this.msg.length >= 3 && (!isNumeric(2) || this.getInt(2) > CommandTeam.MAX_TEAM_SIZE || this.getInt(2) > this.playerData.getTeam().size()))
        {
            this.embed.setDescription("Error with size. Either it isn't a number, larger than the max of " + CommandTeam.MAX_TEAM_SIZE + ", or larger than your team's size!");
            return this;
        }

        boolean checkTeam = !Arrays.asList(this.msg).contains("--nolimit") || Arrays.asList(this.msg).contains("--limit");

        boolean accept = this.msg[1].equals("accept");
        boolean deny = this.msg[1].equals("deny");

        if(DuelHelper.isInDuel(this.player.getId()) && (accept || deny))
        {
            if(accept)
            {
                Duel d = DuelHelper.instance(this.player.getId());

                if(d.getSize() > this.playerData.getTeam().size())
                {
                    this.embed.setDescription("Your team needs to contain at least " + d.getSize() + " to participate! Deleting duel request!");
                    DuelHelper.delete(this.player.getId());
                    return this;
                }
                else if(checkTeam && this.isInvalidTeam(d.getSize()))
                {
                    this.embed.setDescription("Your team exceeds either the Legendary or Mythical limits! For a size " + d.getSize() + " duel, you can have a maximum of " + this.getLegendaryCap(d.getSize()) + " legendaries and " + this.getMythicalUBCap(d.getSize()) + " mythicals/ultra beasts!");
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

        if(this.msg.length >= 3) size = this.getInt(2);

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
        else if(checkTeam && this.isInvalidTeam(size))
        {
            this.embed.setDescription("Your team exceeds either the Legendary or Mythical limits! For a size " + size + " duel, you can have a maximum of " + this.getLegendaryCap(size) + " legendaries and " + this.getMythicalUBCap(size) + " mythicals/ultra beasts!");
            return this;
        }

        Duel d = Duel.create(this.player.getId(), opponentID, size, this.event);

        this.event.getChannel().sendMessage("<@" + opponentID + "> ! " + this.player.getName() + " has challenged you to a duel! Type `p!duel accept` to accept!").queue();
        this.embed = null;
        return this;
    }

    private int getLegendaryCap(int size)
    {
        return 1 + (size - 1) / 4;
    }

    private int getMythicalUBCap(int size)
    {
        return 2 + (size - 1) / 4;
    }

    public boolean isInvalidTeam(int size)
    {
        if(size < 3) return false;

        int legendary = 0;
        int mythical = 0;
        int ub = 0;

        String name;
        List<String> team = this.playerData.getTeam();

        for(int i = 0; i < this.playerData.getTeam().size(); i++)
        {
            name = Mongo.PokemonData.find(Filters.eq("UUID", team.get(i))).first().getString("name");

            if(PokemonRarity.LEGENDARY.contains(name)) legendary++;
            if(PokemonRarity.MYTHICAL.contains(name)) mythical++;
            if(PokemonRarity.ULTRA_BEAST.contains(name)) ub++;
        }

        return legendary > this.getLegendaryCap(size) || (mythical + ub) > this.getMythicalUBCap(size);
    }
}
