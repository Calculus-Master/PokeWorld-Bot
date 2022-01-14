package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.Location;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.MoveData;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.helpers.CSVHelper;
import com.calculusmaster.pokecord.util.helpers.CacheHelper;
import com.calculusmaster.pokecord.util.helpers.ConfigHelper;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.calculusmaster.pokecord.util.helpers.event.LocationEventHelper;
import com.calculusmaster.pokecord.util.helpers.event.RaidEventHelper;
import com.calculusmaster.pokecord.util.helpers.event.SpawnEventHelper;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.collections4.ListUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CommandDev extends Command
{
    public CommandDev(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(!this.player.getId().equals("309135641453527040") && !this.player.getId().equals("423602074811367434"))
        {
            this.response = "You cannot use this command!";
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
                for (int i = 0; i < 15; i++)
                    sb.append(Move.INCOMPLETE_MOVES.get(new Random().nextInt(count))).append("   ");
                this.response = "Moves: " + sb + "\nTotal Remaining: " + count;
                Move.init();
            }
            case "allmoves" -> {
                List<StringBuilder> sbs = new ArrayList<>();
                List<List<String>> chunked = ListUtils.partition(Move.INCOMPLETE_MOVES, 40);

                for(List<String> list : chunked)
                {
                    StringBuilder s = new StringBuilder();
                    for(String m : list) s.append(m).append("              ");
                    sbs.add(s);
                }

                for(StringBuilder s : sbs) Executors.newScheduledThreadPool(1).schedule(() -> this.event.getChannel().sendMessage(s.toString()).queue(), new Random().nextInt(20) + 5, TimeUnit.SECONDS);

                this.response = "Total Remaining: " + Move.INCOMPLETE_MOVES.size();
                Move.init();
            }
            case "clearduels" -> DuelHelper.DUELS.clear();
            case "deletepursuit" -> {
                PlayerDataQuery query = this.mentions.size() > 0 ? PlayerDataQuery.of(this.mentions.get(0).getId()) : this.playerData;
                query.removePursuit();
            }
            case "close" -> Pokecord.close();
            case "reloadconfig" -> ConfigHelper.init();
            case "forcelocation" -> {
                Location l = Location.cast(this.msg[2]);
                if(l != null) LocationEventHelper.forceLocation(this.server, l);
            }
            case "forceraid" -> RaidEventHelper.forceRaid(this.server, this.event.getTextChannel());
            case "restartspawns" -> {
                for(Guild g : Pokecord.BOT_JDA.getGuilds())
                {
                    try
                    {
                        SpawnEventHelper.removeServer(g.getId());
                        Thread.sleep(1000);
                        SpawnEventHelper.start(g);
                    }
                    catch (Exception e)
                    {
                        LoggerHelper.reportError(CommandDev.class, "Could not restart Spawn Event in " + g.getName() + "!", e);
                    }
                }
            }
            case "clearcrashreports" -> Mongo.CrashData.deleteMany(Filters.exists("source"));
        }

        this.response = "Successfully ran Developer Command!";
        return this;
    }

    public static void main(String[] args) throws IOException
    {
        CSVHelper.init();
        MoveData.init();
        Move.init();

        List<Move> incomplete = Move.INCOMPLETE_MOVES.stream().map(Move::new).toList();

        FileWriter writer = new FileWriter("incomplete_moves.txt");

        Arrays.stream(Type.values()).forEach(t -> {
            try { writer.write("Type: " + t.getStyledName() + ":\n" + incomplete.stream().filter(m -> m.getType().equals(t)).map(Move::getName).collect(Collectors.joining("\n")) + "\n\n"); }
            catch (IOException e) { e.printStackTrace(); }
        });

        writer.write("Amount: " + Move.INCOMPLETE_MOVES.size());
        writer.close();

        System.out.println("Wrote Incomplete Moves to \"incomplete_moves.txt\"");
    }
}
