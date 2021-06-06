package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Achievements;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.SpawnEventHandler;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bson.Document;

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
            String poke = spawn;
            if(spawn.contains("Shiny")) poke = spawn.substring("Shiny ".length() + 1);

            Pokemon caught = Pokemon.create(poke);
            caught.setLevel(new Random().nextInt(20 + 5 * (this.playerData.getGymLevel() - 1)) + 1);
            caught.setShiny(spawn.contains("Shiny"));

            Pokemon.uploadPokemon(caught);
            this.playerData.addPokemon(caught.getUUID());

            if(!CommandDex.isForm(caught.getName()))
            {
                Mongo.DexData.updateOne(Filters.eq("name", caught.getName()), Updates.inc(this.player.getId(), 1));

                Document d = Mongo.DexData.find(Filters.eq("name", caught.getName())).first();

                int numCaught = d != null && d.containsKey(this.playerData.getID()) ? d.getInteger(this.playerData.getID()) : 1;

                if(numCaught % 5 == 0)
                {
                    int credits = 200 + 50 * (numCaught / 5 - 1);
                    this.playerData.changeCredits(credits);

                    this.event.getChannel().sendMessage(this.playerData.getMention() + ": You earned " + credits + " credits for reaching a Collection Milestone: **" + numCaught + "** " + caught.getName() + "!").queue();
                }
                else if(numCaught == 1)
                {
                    int credits = 150;
                    this.playerData.changeCredits(credits);

                    this.event.getChannel().sendMessage(this.playerData.getMention() + ": You earned " + credits + " credits for unlocking a Collection: " + caught.getName() + "!").queue();
                }
            }

            if(caught.getTotalIVRounded() >= 90)
            {
                this.playerData.changeRedeems(1);

                this.event.getChannel().sendMessage(this.playerData.getMention() + ": You earned a redeem for catching a high IV pokemon!").queue();
            }

            Achievements.grant(this.player.getId(), Achievements.CAUGHT_FIRST_POKEMON, this.event);

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
