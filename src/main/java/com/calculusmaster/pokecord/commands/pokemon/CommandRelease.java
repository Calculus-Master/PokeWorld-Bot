package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Pokemon;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

public class CommandRelease extends Command
{
    private static Map<String, Integer> releaseRequests = new HashMap<>();

    public CommandRelease(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "release <index>");
    }

    @Override
    public Command runCommand()
    {
        if(!isLength(2))
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }
        else if(!isNumeric(1) && !this.msg[1].equals("confirm") && !this.msg[1].equals("deny"))
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }
        else if(!isNumeric(1) && !this.msg[1].equals("confirm") && !this.msg[1].equals("deny") && this.playerData.getPokemonList().length() <= Integer.parseInt(this.msg[1]))
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        if(releaseRequests.containsKey(this.player.getId()) && this.msg[1].equals("confirm"))
        {
            Pokemon p = Pokemon.build(this.playerData.getPokemonList().getString(releaseRequests.get(this.player.getId()) - 1));

            this.playerData.removePokemon(releaseRequests.get(this.player.getId()));
            Pokemon.deletePokemon(p);
            releaseRequests.remove(this.player.getId());

            this.embed = null;
            this.event.getChannel().sendMessage(this.playerData.getMention() + ": Released Level " + p.getLevel() + " " + p.getName()).queue();
        }
        else if(releaseRequests.containsKey(this.player.getId()) && this.msg[1].equals("deny"))
        {
            releaseRequests.remove(this.player.getId());

            this.embed = null;
            this.event.getChannel().sendMessage(this.playerData.getMention() + ": Cancelled release!").queue();
        }
        else
        {
            if(this.playerData.getPokemonList().length() == 1)
            {
                this.event.getChannel().sendMessage(this.playerData.getMention() + ": You can't release only Pokemon!").queue();
                return this;
            }

            releaseRequests.put(this.player.getId(), this.getInt(1));
            Pokemon p = Pokemon.buildCore(this.playerData.getPokemonList().getString(releaseRequests.get(this.player.getId()) - 1), -1);

            this.event.getChannel().sendMessage("Do you want to release your Level " + p.getLevel() + " " + p.getName() + "? (Type `p!release confirm` or `p!release deny` to continue)").queue();
        }
        return this;
    }
}
