package com.calculusmaster.pokecord;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.economy.CommandMarket;
import com.calculusmaster.pokecord.commands.pokemon.CommandPokemon;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.util.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class Pokecord
{
    public static void main(String[] args) throws InterruptedException, LoginException
    {
        //Disable MongoDB Logging
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver.connection").setLevel(Level.OFF);
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver.management").setLevel(Level.OFF);
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver.cluster").setLevel(Level.OFF);
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver.protocol.insert").setLevel(Level.OFF);
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver.protocol.query").setLevel(Level.OFF);
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver.protocol.update").setLevel(Level.OFF);

        //Initializations

        Global.logInfo(Pokecord.class, "main", "Starting Pokemon Init!");
        Pokemon.init();
        Global.logInfo(Pokecord.class, "main", "Starting Move Init!");
        Move.init();
        Global.logInfo(Pokecord.class, "main", "Starting PokemonRarity Init!");
        PokemonRarity.init();
        Global.logInfo(Pokecord.class, "main", "Starting Command Init!");
        Command.init();
        Global.logInfo(Pokecord.class, "main", "Starting Market Init!");
        CommandMarket.init();
        Global.logInfo(Pokecord.class, "main", "Starting CommandPokemon Init!");
        //CommandPokemon.init();
        CommandPokemon.threadInit();
        Global.logInfo(Pokecord.class, "main", "Completed Init!");

        //Create Bot
        JDABuilder bot = JDABuilder.createDefault(PrivateInfo.TOKEN);
        bot.setActivity(Activity.playing("Pokemon"));
        bot.addEventListeners(new Listener());

        JDA botJDA = bot.build().awaitReady();

        for(Guild g : botJDA.getGuilds())
        {
            Thread.sleep(1000);
            SpawnEventHandler.start(g);
        }
    }
}
