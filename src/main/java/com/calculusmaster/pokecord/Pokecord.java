package com.calculusmaster.pokecord;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.duel.CommandWildDuel;
import com.calculusmaster.pokecord.commands.economy.CommandMarket;
import com.calculusmaster.pokecord.commands.pokemon.CommandPokemon;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.duel.elements.Trainer;
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
        long start = System.currentTimeMillis();

        Global.logInfo(Pokecord.class, "main", "Starting Pokemon Init!");
        Pokemon.init();
        Global.logInfo(Pokecord.class, "main", "Starting CommandWildDuel EV Lists Init!");
        CommandWildDuel.init();
        Global.logInfo(Pokecord.class, "main", "Starting setDailyTrainers!");
        Trainer.setDailyTrainers();
        Global.logInfo(Pokecord.class, "main", "Starting Move Init!");
        Move.init();
        Global.logInfo(Pokecord.class, "main", "Starting PokemonRarity Init!");
        PokemonRarity.init();
        Global.logInfo(Pokecord.class, "main", "Starting Command Init!");
        Command.init();
        Global.logInfo(Pokecord.class, "main", "Starting Market Init!");
        CommandMarket.init();
        Global.logInfo(Pokecord.class, "main", "Starting CommandPokemon Init!");
        CommandPokemon.init();
        Global.logInfo(Pokecord.class, "main", "Completed Init!");

        long end = System.currentTimeMillis();
        System.out.println("Initialization finished in " + (end - start) + "ms!");

        //Create Bot
        JDABuilder bot = JDABuilder.createDefault(PrivateInfo.TOKEN);
        bot.setActivity(Activity.playing("Pokemon"));
        bot.addEventListeners(new Listener());

        JDA botJDA = bot.build().awaitReady();

        end = System.currentTimeMillis();
        System.out.println("Loading finished in " + (end - start) + "ms!");

        for(Guild g : botJDA.getGuilds())
        {
            Thread.sleep(1000);
            SpawnEventHandler.start(g);
        }
    }
}
