package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.Location;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.helpers.CacheHelper;
import com.calculusmaster.pokecord.util.helpers.ConfigHelper;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.calculusmaster.pokecord.util.helpers.event.LocationEventHelper;
import com.calculusmaster.pokecord.util.helpers.event.RaidEventHelper;
import com.calculusmaster.pokecord.util.helpers.event.SpawnEventHelper;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

public class CommandDev extends Command
{
    public CommandDev(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(!this.player.getId().equals("309135641453527040"))
        {
            this.sendMsg("You cannot use this command!");
            return this;
        }

        switch(this.msg[1])
        {
            case "forcespawn" -> {
                String spawn;
                if (this.msg[2].equals("random")) spawn = PokemonRarity.getSpawn();
                else if (this.msg[2].equals("legendary")) spawn = PokemonRarity.getLegendarySpawn();
                else spawn = this.getMultiWordContent(2);
                SpawnEventHelper.forceSpawn(this.server, spawn);
            }
            case "deletebotmarket" -> {
                Mongo.MarketData.deleteMany(Filters.eq("sellerID", "BOT"));
                CacheHelper.MARKET_ENTRIES.clear();
                CacheHelper.initMarketEntries();
            }
            case "randommoves" -> {
                StringBuilder sb = new StringBuilder();
                int count = Move.INCOMPLETE_MOVES.size();
                for (int i = 0; i < 5; i++)
                    sb.append(Move.INCOMPLETE_MOVES.get(new Random().nextInt(count))).append("   ");
                this.sendMsg("Moves: " + sb + "\nTotal Remaining: " + count);
                Move.init();
            }
            case "clearduels" -> DuelHelper.DUELS.clear();
            case "deletepursuit" -> {
                PlayerDataQuery query = this.mentions.size() > 0 ? new PlayerDataQuery(this.mentions.get(0).getId()) : this.playerData;
                query.removePursuit();
            }
            case "close", "shutdown", "stop", "quit" -> Pokecord.close();
            case "reloadconfig" -> ConfigHelper.init();
            case "reloadpokemondata" -> DataHelper.createPokemonData();
            case "forcelocation" -> {
                Location l = Location.cast(this.msg[2]);
                if(l != null) LocationEventHelper.forceLocation(this.server, l);
            }
            case "forceraid" -> RaidEventHelper.forceRaid(this.server, this.event.getTextChannel());
            case "restartspawns" -> {
                for(Guild g : Pokecord.BOT_JDA.getGuilds())
                {
                    try {
                        SpawnEventHelper.removeServer(g.getId());
                        Thread.sleep(1000);
                        SpawnEventHelper.start(g);
                    } catch (Exception e) {
                        LoggerHelper.error(CommandDev.class, "Could not restart Spawn Event in " + g.getName() + "!");
                        e.printStackTrace();
                    }
                }
            }
        }

        this.sendMsg("Successfully ran Developer Command!");
        return this;
    }
}
