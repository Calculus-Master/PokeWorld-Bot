package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.util.SpawnEventHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

public class CommandCatch extends Command
{
    public CommandCatch(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "catch <name>");
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length != 2 && this.msg.length != 3)
        {
            this.embed.setDescription(CommandInvalid.getShort());
        }
        else if(SpawnEventHandler.getSpawn(this.server.getId()).isEmpty())
        {
            this.event.getChannel().sendMessage("<@" + this.player.getId() + ">: Nothing has spawned yet in this server!").queue();
            this.embed = null;
        }
        else if(!(this.msg[1] + (this.msg.length == 3 ? " " + this.msg[2] : "")).toLowerCase().equals(SpawnEventHandler.getSpawn(this.server.getId()).toLowerCase()))
        {
            this.event.getChannel().sendMessage("<@" + this.player.getId() + ">: Incorrect name!").queue();
            this.embed = null;
        }
        else
        {
            Pokemon caught = Pokemon.create(SpawnEventHandler.getSpawn(this.server.getId()));
            caught.setLevel(new Random().nextInt(42) + 1);

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
}
