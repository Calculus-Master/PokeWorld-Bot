package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Achievements;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.mongo.CollectionsQuery;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.helpers.SpawnEventHelper;
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

        String spawn = SpawnEventHelper.getSpawn(this.server.getId());
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
            String poke = spawn.contains("Shiny") ? spawn.substring("Shiny ".length()) : spawn;

            Pokemon caught = Pokemon.create(poke);
            caught.setLevel(new Random().nextInt(20 + 5 * (this.playerData.getGymLevel() - 1)) + 1);
            caught.setShiny(spawn.contains("Shiny"));

            Pokemon.uploadPokemon(caught);
            this.playerData.addPokemon(caught.getUUID());

            //Collections
            CollectionsQuery collection = new CollectionsQuery(caught.getName(), this.player.getId());

            collection.increase();
            int amount = collection.getCaughtAmount();

            if(amount == -1) this.sendMsg("An error has occurred with collections!");
            else if(amount % 5 == 0)
            {
                int credits = 200 + 100 * (amount / 5 - 1);
                this.playerData.changeCredits(credits);

                this.sendMsg("You earned " + credits + " credits for reaching a Collection Milestone: **" + amount + "** " + caught.getName() + "!");
            }
            else if(amount == 1)
            {
                int credits = 150;
                this.playerData.changeCredits(credits);

                this.sendMsg("You earned " + credits + " credits for unlocking a Collection: " + caught.getName() + "!");
            }

            if(caught.getTotalIVRounded() >= 90 || (caught.getTotalIVRounded() >= 80 && new Random().nextInt(100) < 20))
            {
                this.playerData.changeRedeems(1);

                this.sendMsg("You earned a redeem for catching a Pokemon with high IVs!");
            }

            Achievements.grant(this.player.getId(), Achievements.CAUGHT_FIRST_POKEMON, this.event);
            this.playerData.addPokePassExp(100, this.event);

            this.sendMsg("You caught a **Level " + caught.getLevel() + " " + caught.getName() + "** (Collection: " + amount + ")!");

            SpawnEventHelper.clearSpawn(this.server.getId());
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
