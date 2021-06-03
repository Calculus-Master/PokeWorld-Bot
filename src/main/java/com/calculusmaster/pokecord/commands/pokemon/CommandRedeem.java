package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

public class CommandRedeem extends Command
{
    public CommandRedeem(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.playerData.getRedeems() < 1)
        {
            this.event.getChannel().sendMessage(this.playerData.getMention() + ": You don't have any redeems!").queue();
            this.embed = null;
            return this;
        }
        else if(this.msg.length >= 2 && Global.POKEMON.contains(this.getPokemon()))
        {
            if(!this.player.getId().equals("309135641453527040") && this.getPokemon().contains("Necrozma"))
            {
                this.event.getChannel().sendMessage(this.playerData.getMention() + ": You don't have any redeems!").queue();
                this.embed = null;
                return this;
            }

            this.playerData.changeRedeems(-1);

            Pokemon p = Pokemon.create(this.getPokemon());
            p.setLevel(new Random().nextInt(100) + 1);

            Pokemon.uploadPokemon(p);
            this.playerData.addPokemon(p.getUUID());

            this.event.getChannel().sendMessage(this.playerData.getMention() + ": You acquired a Level " + p.getLevel() + " " + p.getName() + "!").queue();
            this.embed = null;
            return this;
        }
        else
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }
    }

    private String getPokemon()
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < this.msg.length; i++) sb.append(this.msg[i]).append(" ");

        return Global.normalCase(sb.toString().trim());
    }
}
