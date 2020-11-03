package com.calculusmaster.pokecord.util;

import com.calculusmaster.pokecord.commands.*;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Listener extends ListenerAdapter
{
    private OffsetDateTime catchTimestamp = null;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        //Check if the message was sent by a bot, and skip the listener if true
        if(event.getAuthor().isBot()) return;

        User player = event.getAuthor();
        Guild server = event.getGuild();
        String[] msg = event.getMessage().getContentRaw().toLowerCase().trim().split("\\s+");
        ServerDataQuery serverQuery;
        Command c;
        Random r = new Random(System.currentTimeMillis());

        //Check if a pokemon is able to spawn given the correct time between
        //TODO: Replace this with the schedule Time Task thing
        if(r.nextInt(15) < 2 && (catchTimestamp == null || catchTimestamp.getMinute() - event.getMessage().getTimeCreated().getMinute() >= 3))
        {
            spawnEvent(event);
            catchTimestamp = event.getMessage().getTimeCreated();
        }

        //Check if the server is registered and register it if it is not
        if(!ServerDataQuery.isRegistered(server)) ServerDataQuery.register(server, event.getChannel().getId());

        //Create a query object for server data
        serverQuery = new ServerDataQuery(server.getId());

        //Set a boolean if the player is registered or not
        boolean isPlayerRegistered = PlayerDataQuery.isRegistered(player);

        //If the 'selected' field is out of bounds, force it into bounds to avoid errors
        if(isPlayerRegistered) new PlayerDataQuery(player.getId()).updateSelected();

        //If the message starts with the right prefix, continue, otherwise skip the listener
        if(msg[0].startsWith(serverQuery.getPrefix()))
        {
            event.getChannel().sendTyping().queue();

            //Remove prefix from the message array, msg[0] is the raw command name
            msg[0] = msg[0].substring(serverQuery.getPrefix().length());

            //Check for a valid command, and if there is none reply with the invalid message
            if(Command.CMD_START.contains(msg[0]) || !isPlayerRegistered)
            {
                c = new CommandStart(event, msg).runCommand();
            }
            else if(Command.CMD_SPAWNCHANNEL.contains(msg[0]))
            {
                c = new CommandSpawnChannel(event, msg).runCommand();
            }
            else if(Command.CMD_BALANCE.contains(msg[0]))
            {
                c = new CommandBalance(event, msg).runCommand();
            }
            else if(Command.CMD_SELECT.contains(msg[0]))
            {
                c = new CommandSelect(event, msg).runCommand();
            }
            else if(Command.CMD_DEX.contains(msg[0]))
            {
                c = new CommandDex(event, msg).runCommand();
            }
            else if(Command.CMD_INFO.contains(msg[0]))
            {
                c = new CommandInfo(event, msg).runCommand();
            }
            else if(Command.CMD_CATCH.contains(msg[0]))
            {
                c = new CommandCatch(event, msg).runCommand();
            }
            else if(Command.CMD_POKEMON.contains(msg[0]))
            {
                c = new CommandPokemon(event, msg).runCommand();
            }
            else if(Command.CMD_MOVES.contains(msg[0]))
            {
                c = new CommandMoves(event, msg).runCommand();
            }
            else if(Command.CMD_LEARN.contains(msg[0]))
            {
                c = new CommandLearn(event, msg).runCommand();
            }
            else if(Command.CMD_REPLACE.contains(msg[0]))
            {
                c = new CommandReplace(event, msg).runCommand();
            }
            else if(Command.CMD_MOVEINFO.contains(msg[0]))
            {
                c = new CommandMoveInfo(event, msg).runCommand();
            }
            else if(Command.CMD_DUEL.contains(msg[0]))
            {
                try {
                    c = new CommandDuel(event, msg).runCommand();
                } catch (IOException e) {
                    c = new CommandInvalid(event, msg).runCommand();
                }
            }
            else if(Command.CMD_USE.contains(msg[0]))
            {
                try {
                    c = new CommandUse(event, msg).runCommand();
                } catch(IOException e) {
                    c = new CommandInvalid(event, msg).runCommand();
                }
            }
            else if(Command.CMD_SHOP.contains(msg[0]))
            {
                c = new CommandShop(event, msg).runCommand();
            }
            else if(Command.CMD_BUY.contains(msg[0]))
            {
                c = new CommandBuy(event, msg).runCommand();
            }
            else if(Command.CMD_RELEASE.contains(msg[0]))
            {
                c = new CommandRelease(event, msg).runCommand();
            }
            else if(Command.CMD_REPORT.contains(msg[0]))
            {
                c = new CommandReport(event, msg).runCommand();
            }
            else c = new CommandInvalid(event, msg).runCommand();

            if(c.getResponseEmbed() != null) event.getChannel().sendMessage(c.getResponseEmbed()).queue();
        }

        if(r.nextInt(10) <= 3) Listener.expEvent(event);
    }

    private static void spawnEvent(MessageReceivedEvent event)
    {
        String spawnPokemon = PokemonRarity.getSpawn();
        ServerDataQuery data = new ServerDataQuery(event.getGuild().getId());
        data.setSpawn(Global.normalCase(spawnPokemon));

        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("A wild Pokemon spawned!");
        embed.setDescription("Try to guess its name and catch it with p!catch <name>!");
        embed.setImage(Pokemon.genericJSON(Global.normalCase(spawnPokemon)).getString("normalURL"));

        event.getGuild().getTextChannelById(data.getSpawnChannelID()).sendMessage(embed.build()).queue();
    }

    public static class SpawnEvent extends TimerTask
    {
        //TODO: SpawnEvent with TimerTask
        private static final Timer timer = new Timer();
        @Override
        public void run()
        {
            timer.schedule(new SpawnEvent(), SpawnEvent.getDelay());
            System.out.println("Spawning Pokemon");
        }

        private static long getDelay()
        {
            return 1000L * 60 * (3 + new Random().nextInt(8));
        }
    }

    private static void expEvent(MessageReceivedEvent event)
    {
        PlayerDataQuery data = new PlayerDataQuery(event.getAuthor().getId());
        Pokemon p = data.getSelectedPokemon();

        int initL = p.getLevel();

        p.addExp(new Random().nextInt(100));

        if(p.getLevel() != initL)
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(event.getAuthor().getName());
            embed.setDescription("Your " + p.getName() + " leveled up to Level " + p.getLevel() + "!");
            event.getChannel().sendMessage(embed.build()).queue();
        }

        if(p.canEvolve())
        {
            String old = p.getName();
            p.evolve();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(event.getAuthor().getName());
            embed.setDescription("Your " + old + " evolved into a " + p.getName() + "!");
            event.getChannel().sendMessage(embed.build()).queue();
        }

        Pokemon.updateExperience(p);
    }
}
