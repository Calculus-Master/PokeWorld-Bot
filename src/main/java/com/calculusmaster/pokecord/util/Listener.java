package com.calculusmaster.pokecord.util;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.commands.config.CommandPrefix;
import com.calculusmaster.pokecord.commands.config.CommandSpawnChannel;
import com.calculusmaster.pokecord.commands.duel.CommandDuel;
import com.calculusmaster.pokecord.commands.duel.CommandUse;
import com.calculusmaster.pokecord.commands.economy.*;
import com.calculusmaster.pokecord.commands.misc.CommandHelp;
import com.calculusmaster.pokecord.commands.misc.CommandReport;
import com.calculusmaster.pokecord.commands.misc.CommandStart;
import com.calculusmaster.pokecord.commands.misc.CommandTrade;
import com.calculusmaster.pokecord.commands.moves.*;
import com.calculusmaster.pokecord.commands.pokemon.*;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.items.XPBooster;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.*;

public class Listener extends ListenerAdapter
{
    private final Map<String, Boolean> hasSpawnEventRunning = new HashMap<>();
    private final Map<String, SpawnEvent> spawnEventMap = new HashMap<>();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        //Check if the message was sent by a bot, and skip the listener if true
        if(event.getAuthor().isBot()) return;

        //If a link is sent, skip the listener
        if(event.getMessage().getContentRaw().toLowerCase().startsWith("http")) return;

        //If any attachment is sent, skip the listener
        if(event.getMessage().getAttachments().size() > 0) return;

        User player = event.getAuthor();
        Guild server = event.getGuild();
        String[] msg = event.getMessage().getContentRaw().toLowerCase().trim().split("\\s+");
        ServerDataQuery serverQuery;
        Command c;
        Random r = new Random(System.currentTimeMillis());

        //Check if the server is registered and register it if it is not
        if(!ServerDataQuery.isRegistered(server)) ServerDataQuery.register(server, event.getChannel().getId());

        //Create a query object for server data
        serverQuery = new ServerDataQuery(server.getId());

        //Spawn Pokemon
        if(!this.hasSpawnEventRunning.containsKey(server.getId()))
        {
            this.hasSpawnEventRunning.put(server.getId(), true);
            this.spawnEventMap.put(server.getId(), new SpawnEvent(server, serverQuery.getSpawnChannelID()));
            this.spawnEventMap.get(server.getId()).run();
        }

        //Set a boolean if the player is registered or not
        boolean isPlayerRegistered = PlayerDataQuery.isRegistered(player);
        //System.out.println(player.getName() + " Registered? : " + isPlayerRegistered);

        //If the 'selected' field is out of bounds, force it into bounds to avoid errors
        if(isPlayerRegistered) new PlayerDataQuery(player.getId()).updateSelected();

        //Check the xp booster timestamp of the player who sent the message
        if(isPlayerRegistered) Listener.checkXPTimeStamp(event);

        //If the message starts with the right prefix, continue, otherwise skip the listener
        if(msg[0].startsWith(serverQuery.getPrefix()))
        {
            //event.getChannel().sendTyping().queue();

            Global.logInfo(this.getClass(), "getResponseEmbed", "Parsing: " + Arrays.toString(msg));

            //Remove prefix from the message array, msg[0] is the raw command name
            msg[0] = msg[0].substring(serverQuery.getPrefix().length());

            //Check for a valid command, and if there is none reply with the invalid message
            if(Command.START.contains(msg[0]) || !isPlayerRegistered)
            {
                c = new CommandStart(event, msg).runCommand();
            }
            else if(Command.SPAWNCHANNEL.contains(msg[0]))
            {
                c = new CommandSpawnChannel(event, msg).runCommand();
            }
            else if(Command.BALANCE.contains(msg[0]))
            {
                c = new CommandBalance(event, msg).runCommand();
            }
            else if(Command.SELECT.contains(msg[0]))
            {
                c = new CommandSelect(event, msg).runCommand();
            }
            else if(Command.DEX.contains(msg[0]))
            {
                c = new CommandDex(event, msg).runCommand();
            }
            else if(Command.INFO.contains(msg[0]))
            {
                c = new CommandInfo(event, msg).runCommand();
            }
            else if(Command.CATCH.contains(msg[0]))
            {
                c = new CommandCatch(event, msg).runCommand();
            }
            else if(Command.POKEMON.contains(msg[0]))
            {
                c = new CommandPokemon(event, msg).runCommand();
            }
            else if(Command.MOVES.contains(msg[0]))
            {
                c = new CommandMoves(event, msg).runCommand();
            }
            else if(Command.LEARN.contains(msg[0]))
            {
                c = new CommandLearn(event, msg).runCommand();
            }
            else if(Command.REPLACE.contains(msg[0]))
            {
                c = new CommandReplace(event, msg).runCommand();
            }
            else if(Command.MOVEINFO.contains(msg[0]))
            {
                c = new CommandMoveInfo(event, msg).runCommand();
            }
            else if(Command.DUEL.contains(msg[0]))
            {
                try {
                    c = new CommandDuel(event, msg).runCommand();
                } catch (IOException e) {
                    c = new CommandInvalid(event, msg).runCommand();
                }
            }
            else if(Command.USE.contains(msg[0]))
            {
                try {
                    c = new CommandUse(event, msg).runCommand();
                } catch(IOException e) {
                    c = new CommandInvalid(event, msg).runCommand();
                }
            }
            else if(Command.SHOP.contains(msg[0]))
            {
                c = new CommandShop(event, msg).runCommand();
            }
            else if(Command.BUY.contains(msg[0]))
            {
                c = new CommandBuy(event, msg).runCommand();
            }
            else if(Command.RELEASE.contains(msg[0]))
            {
                c = new CommandRelease(event, msg).runCommand();
            }
            else if(Command.REPORT.contains(msg[0]))
            {
                c = new CommandReport(event, msg).runCommand();
            }
            else if(Command.TEACH.contains(msg[0]))
            {
                c = new CommandTeach(event, msg).runCommand();
            }
            else if(Command.INVENTORY.contains(msg[0]))
            {
                c = new CommandInventory(event, msg).runCommand();
            }
            else if(Command.HELP.contains(msg[0]))
            {
                c = new CommandHelp(event, msg).runCommand();
            }
            else if(Command.PREFIX.contains(msg[0]))
            {
                c = new CommandPrefix(event, msg).runCommand();
            }
            else if(Command.TRADE.contains(msg[0]))
            {
                c = new CommandTrade(event, msg).runCommand();
            }
            else if(Command.GIVE.contains(msg[0]))
            {
                c = new CommandGive(event, msg).runCommand();
            }
            else if(Command.MARKET.contains(msg[0]))
            {
                c = new CommandMarket(event, msg).runCommand();
            }
            else if(Command.EVOLVE.contains(msg[0]))
            {
                c = new CommandEvolve(event, msg).runCommand();
            }
            else if(msg[0].equals("ness"))
            {
                event.getGuild().getMemberById("423603232971948044").modifyNickname("I <3 Penis").queue();
                c = null;
            }
            //Debug Commands
            else if(player.getId().equals("309135641453527040"))
            {
                if(msg[0].equals("deleteperformance"))
                {
                    Mongo.PerformanceData.deleteMany(Filters.exists("command"));
                    c = null;
                }
                else if(msg[0].equals("deletepokemon"))
                {
                    Mongo.PokemonData.deleteMany(Filters.exists("name"));
                    c = null;
                }
                else if(msg[0].equals("deleteplayers"))
                {
                    Mongo.PlayerData.deleteMany(Filters.exists("name"));
                    c = null;
                }
                else if(msg[0].equals("forcespawn"))
                {
                    this.spawnEventMap.get(server.getId()).cancel();

                    String spawn;

                    if(msg[1].equals("random")) spawn = PokemonRarity.getSpawn();
                    else if(msg[1].equals("legendary")) spawn = PokemonRarity.getLegendarySpawn();
                    else spawn = msg.length > 2 ? msg[1] + " " + msg[2] : msg[1];

                    this.spawnEventMap.put(server.getId(), new SpawnEvent(server, serverQuery.getSpawnChannelID(), spawn));
                    this.spawnEventMap.get(server.getId()).run();
                    c = null;
                }
                else if(msg[0].equals("deletebotmarket"))
                {
                    Mongo.MarketData.deleteMany(Filters.eq("sellerID", "BOT"));
                    c = null;
                }
                else c = new CommandInvalid(event, msg).runCommand();
            }
            else c = new CommandInvalid(event, msg).runCommand();

            if(c == null) event.getChannel().sendMessage("Debug command successfully run!").queue();
            else if(!c.isNull()) event.getChannel().sendMessage(c.getResponseEmbed()).queue();
        }

        if(r.nextInt(10) <= 3) Listener.expEvent(event);
    }

    public static class SpawnEvent extends TimerTask
    {
        private static final Timer timer = new Timer();
        private final Guild server;
        private final String spawnChannel;
        private String forcedSpawn;

        public SpawnEvent(Guild server, String channel)
        {
            this.server = server;
            this.spawnChannel = channel;
            this.forcedSpawn = "";
        }

        public SpawnEvent(Guild server, String channel, String forced)
        {
            this(server, channel);
            this.forcedSpawn = Global.normalCase(forced);
        }

        @Override
        public void run()
        {
            timer.schedule(new SpawnEvent(this.server, this.spawnChannel), SpawnEvent.getDelay());
            //new Thread(this::spawnEvent).start();
            this.spawnEvent();
            //this.server.getTextChannelById(this.spawnChannel).sendMessage(spawnEvent(this.server).build()).queue();
        }

        private void spawnEvent()
        {
            if(new Random().nextInt(100) < 30) CommandMarket.addBotEntry();

            String spawnPokemon = PokemonRarity.getSpawn();
            ServerDataQuery data = new ServerDataQuery(server.getId());

            if(!this.forcedSpawn.equals("")) spawnPokemon = this.forcedSpawn;

            data.setSpawn(Global.normalCase(spawnPokemon));

            EmbedBuilder embed = new EmbedBuilder();

            embed.setTitle("A wild Pokemon spawned!");
            embed.setDescription("Try to guess its name and catch it with p!catch <name>!");
            embed.setColor(new Color(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)));

            embed.setImage("attachment://pkmn.png");

            try
            {
                URL url = new URL(Pokemon.genericJSON(Global.normalCase(spawnPokemon)).getString("normalURL"));
                BufferedImage img = ImageIO.read(url);
                File file = new File("pokemon.png");
                ImageIO.write(img, "png", file);

                this.server.getTextChannelById(this.spawnChannel).sendFile(file, "pkmn.png").embed(embed.build()).queue();
                if(this.server.getId().equals(PrivateInfo.SERVER_ID_MAIN)) this.server.getTextChannelById("843996103639695360").sendFile(file, "pkmn.png").embed(embed.build()).queue();
            }
            catch (Exception e)
            {
                System.out.println("SPAWN EVENT FAILED!");
                System.out.println(Global.normalCase(spawnPokemon));
                e.printStackTrace();
            }

            this.forcedSpawn = "";
        }

        //TODO: Figure out better spawn rate
        private static long getDelay()
        {
            return 1000L * (60 + new Random().nextInt(3 * 60));
        }
    }

    private static void checkXPTimeStamp(MessageReceivedEvent event)
    {
        PlayerDataQuery data = new PlayerDataQuery(event.getAuthor().getId());
        if(!data.hasXPBooster()) return;

        String[] ts = data.getXPBoosterTimeStamp().split("-");
        int timestamp = Integer.parseInt(ts[0]) * 24 * 60 + Integer.parseInt(ts[1]) * 60 + Integer.parseInt(ts[2]);
        int length = XPBooster.getInstance(data.getXPBoosterLength()).time();

        OffsetDateTime t = event.getMessage().getTimeCreated();
        int timeNow = t.getDayOfYear() * 24 * 60 + t.getHour() * 60 + t.getMinute();

        //System.out.println(timeNow - timestamp);
        //System.out.println(length);
        if(timeNow - timestamp >= length) data.removeXPBooster();
    }

    private static void expEvent(MessageReceivedEvent event)
    {
        PlayerDataQuery data = new PlayerDataQuery(event.getAuthor().getId());
        String mention = "<@" + event.getAuthor().getId() + ">";
        Pokemon p = data.getSelectedPokemon();

        int initL = p.getLevel();
        double booster = data.hasXPBooster() ? XPBooster.getInstance(data.getXPBoosterLength()).boost : 1.0;

        p.addExp((int)(booster * new Random().nextInt(100)));

        if(p.getLevel() != initL)
        {
            event.getChannel().sendMessage(mention + ": Your " + p.getName() + " leveled up to Level " + p.getLevel() + "!").queue();
        }

//        if(p.canEvolve())
//        {
//            String old = p.getName();
//            p.evolve();
//
//            event.getChannel().sendMessage(mention + ": Your " + old + " evolved into a " + p.getName() + "!").queue();
//        }

        Pokemon.updateExperience(p);
    }
}
