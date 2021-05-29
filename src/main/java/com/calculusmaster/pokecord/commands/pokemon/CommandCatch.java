package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.SpawnEventHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

public class CommandCatch extends Command
{
    public CommandCatch(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length < 2)
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        String spawn = SpawnEventHandler.getSpawn(this.server.getId());
        String guess = this.getPokemon();

        if(spawn.isEmpty())
        {
            this.event.getChannel().sendMessage("<@" + this.player.getId() + ">: Nothing has spawned yet in this server!").queue();
            this.embed = null;
        }
        else if(!guess.equals(spawn))
        {
            this.event.getChannel().sendMessage("<@" + this.player.getId() + ">: Incorrect name!").queue();
            this.embed = null;
        }
        else
        {
            Pokemon caught = Pokemon.create(spawn);
            caught.setLevel(new Random().nextInt(20) + 1);

            new Thread(() -> {
                Pokemon.uploadPokemon(caught);
                this.playerData.addPokemon(caught.getUUID());
            }).start();

            this.embed = null;
            this.event.getChannel().sendMessage("<@" + this.player.getId() + ">: You caught a **Level " + caught.getLevel() + " " + caught.getName() + "**!").queue();

            SpawnEventHandler.clearSpawn(this.server.getId());
        }
        return this;
    }

    private String getPokemon()
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < this.msg.length; i++) sb.append(this.msg[i]).append(" ");
        return Global.normalCase(sb.toString().trim());
    }
}
