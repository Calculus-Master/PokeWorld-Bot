package com.calculusmaster.pokecord.util;

import com.calculusmaster.pokecord.commands.economy.CommandMarket;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SpawnEventHandler
{
    private static final Map<String, String> SERVER_SPAWNS = new HashMap<>();
    private static final Map<String, ScheduledFuture<?>> SCHEDULERS = new HashMap<>();

    public static void start(Guild g)
    {
        start(g, 10);
    }

    public static void start(Guild g, int initDelay)
    {
        TextChannel channel = g.getTextChannelById(new ServerDataQuery(g.getId()).getSpawnChannelID());

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        ScheduledFuture<?> spawnEvent = scheduler.scheduleWithFixedDelay(() -> spawnPokemon(g, channel), initDelay, g.getId().equals("718169905257512960") ? 30 : 100, TimeUnit.SECONDS);

        SCHEDULERS.put(g.getId(), spawnEvent);
    }

    public static String getSpawn(String id)
    {
        return SERVER_SPAWNS.get(id);
    }

    public static void clearSpawn(String id)
    {
        SERVER_SPAWNS.put(id, "");
    }

    private static void spawnPokemon(Guild g, TextChannel channel)
    {
        spawnPokemon(g, channel, PokemonRarity.getSpawn());
    }

    public static void forceSpawn(Guild g, String spawn)
    {
        SCHEDULERS.get(g.getId()).cancel(true);
        SCHEDULERS.remove(g.getId());

        spawnPokemon(g, g.getTextChannelById(new ServerDataQuery(g.getId()).getSpawnChannelID()), spawn);

        start(g, 120);
    }

    private static void spawnPokemon(Guild g, TextChannel channel, String spawn)
    {
        spawn = Global.normalCase(spawn);

        //if(Math.random() < 0.333) new Thread(CommandMarket::addBotEntry).start();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("A wild Pokemon spawned!")
                .setDescription("Try to guess its name and catch it with p!catch <name>!")
                .setColor(new Color(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)))
                .setImage("attachment://pkmn.png");

        try
        {
            URL url = new URL(Pokemon.genericJSON(spawn).getString("normalURL"));
            BufferedImage img = ImageIO.read(url);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(img, "png", out);

            byte[] bytes = out.toByteArray();

            channel.sendFile(bytes, "pkmn.png").embed(embed.build()).queue();
            if(g.getId().equals(PrivateInfo.SERVER_ID_MAIN)) g.getTextChannelById("843996103639695360").sendFile(bytes, "pkmn.png").embed(embed.build()).queue();
        }
        catch (Exception e)
        {
            System.out.println("SPAWN EVENT FAILED trying to spawn " + spawn + "!");
            e.printStackTrace();
        }

        SERVER_SPAWNS.put(g.getId(), spawn);
    }
}
