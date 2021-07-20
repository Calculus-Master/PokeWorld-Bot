package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Achievements;
import com.calculusmaster.pokecord.game.bounties.objectives.CatchNameObjective;
import com.calculusmaster.pokecord.game.bounties.objectives.CatchPoolObjective;
import com.calculusmaster.pokecord.game.bounties.objectives.CatchTypeObjective;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.mongo.CollectionsQuery;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import com.calculusmaster.pokecord.util.helpers.SpawnEventHelper;
import com.calculusmaster.pokecord.util.helpers.ThreadPoolHandler;
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
        long initT = System.currentTimeMillis();
        if(this.msg.length < 2)
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        String spawn = SpawnEventHelper.getSpawn(this.server.getId());
        String guess = this.getPokemon();

        if(spawn.isEmpty()) this.sendMsg("Nothing has spawned yet in this server!");
        else if(!guess.equals(spawn)) this.sendMsg("Incorrect name!");
        else
        {
            String poke = spawn.contains("Shiny") ? spawn.substring("Shiny ".length()) : spawn;

            Pokemon caught = Pokemon.create(poke);
            caught.setLevel(new Random().nextInt(20 + 5 * (this.playerData.getGymLevel() - 1)) + 1);
            if(!caught.isShiny()) caught.setShiny(spawn.contains("Shiny"));

            //Longest 2 methods in this entire command (~100-200 ms each) - Thread Pool?
            ThreadPoolHandler.CATCH.execute(() -> {
                Pokemon.uploadPokemon(caught);
                this.playerData.addPokemon(caught.getUUID());
            });

            //Collections
            CollectionsQuery collection = new CollectionsQuery(caught.getName(), this.player.getId());

            collection.increase();
            int amount = collection.getCaughtAmount();

            if(amount == -1) this.sendMsg("An error has occurred with collections!");
            else if(amount % 5 == 0)
            {
                int credits = 200 + 100 * (amount / 5 - 1);
                this.playerData.changeCredits(credits);

                this.sendMsg("Reached Collection Milestone for " + caught.getName() + ": **" + amount + "** (**" + credits + "**c)!");
            }
            else if(amount == 1)
            {
                int credits = 150;
                this.playerData.changeCredits(credits);

                this.sendMsg("Unlocked Collection for " + caught.getName() + " (**" + credits + "**c)!");
            }

            if(caught.getTotalIVRounded() >= 90 || (caught.getTotalIVRounded() >= 80 && new Random().nextInt(100) < 20))
            {
                this.playerData.changeRedeems(1);

                this.playerData.getStats().incr(PlayerStatistic.NATURAL_REDEEMS_EARNED);

                this.sendMsg("You earned a redeem for catching a Pokemon with high IVs!");
            }

            //Statistics
            this.playerData.getStats().incr(PlayerStatistic.POKEMON_CAUGHT);

            Achievements.grant(this.player.getId(), Achievements.CAUGHT_FIRST_POKEMON, this.event);
            this.playerData.addPokePassExp(100, this.event);

            this.playerData.updateBountyProgression(b -> {
                switch(b.getType()) {
                    case CATCH_POKEMON -> b.update();
                    case CATCH_POKEMON_TYPE -> {
                        if(caught.isType(((CatchTypeObjective)b.getObjective()).getType())) b.update();
                    }
                    case CATCH_POKEMON_NAME -> {
                        if(caught.getName().equals(((CatchNameObjective)b.getObjective()).getName())) b.update();
                    }
                    case CATCH_POKEMON_POOL -> {
                        if(((CatchPoolObjective)b.getObjective()).getPool().contains(caught.getName())) b.update();
                    }
                }
            });

            this.sendMsg("You caught a **Level " + caught.getLevel() + " " + caught.getName() + "** (Collection: " + amount + ")!");

            SpawnEventHelper.clearSpawn(this.server.getId());
        }

        return this;
    }

    private String getPokemon()
    {
        return Global.normalCase(this.getMultiWordContent(1));
    }
}
