package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Achievements;
import com.calculusmaster.pokecord.game.Pokemon;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
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
        else if(this.serverData.getSpawn().equals(""))
        {
            this.embed.setDescription("Nothing has spawned yet in this server!");
            this.color = new Color(0, 0, 0);
        }
        else if(!(this.msg[1] + (this.msg.length == 3 ? " " + this.msg[2] : "")).toLowerCase().equals(this.serverData.getSpawn().toLowerCase()))
        {
            this.embed.setDescription("Incorrect name!");
            this.color = new Color(0, 0, 0);
        }
        else
        {
            Pokemon caught = Pokemon.create(this.serverData.getSpawn());
            caught.setLevel(new Random().nextInt(42) + 1);

            if(this.playerData.getPokemonList() == null) Achievements.grant(this.player.getId(), Achievements.CAUGHT_1ST_POKEMON, this.event);

            new Thread(() -> {
                Pokemon.uploadPokemon(caught);
                this.playerData.addPokemon(caught.getUUID());
            }).start();

            this.embed = null;
            this.event.getChannel().sendMessage("<@" + this.player.getId() + ">: You caught a **Level " + caught.getLevel() + " " + caught.getName() + "**!").queue();

            this.serverData.setSpawn("");
        }
        return this;
    }
}
