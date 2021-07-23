package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.commands.economy.CommandMarket;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SpawnEventHelper
{
    public static int RAID_CHANCE;
    public static int SPAWN_INTERVAL;

    private static final Map<String, String> SERVER_SPAWNS = new HashMap<>();
    private static final Map<String, ScheduledFuture<?>> SCHEDULERS = new HashMap<>();

    public static void start(Guild g)
    {
        start(g, 10);
    }

    public static void start(Guild g, int initDelay)
    {
        ScheduledFuture<?> spawnEvent = ThreadPoolHandler.SPAWN.scheduleWithFixedDelay(() -> spawnPokemon(g), initDelay, SPAWN_INTERVAL, TimeUnit.SECONDS);

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

    private static void spawnPokemon(Guild g)
    {
        spawnPokemon(g, PokemonRarity.getSpawn());
    }

    public static void forceSpawn(Guild g, String spawn)
    {
        removeServer(g.getId());

        spawnPokemon(g, spawn);

        start(g, 120);
    }

    private static void spawnPokemon(Guild g, String spawn)
    {
        if(RaidEventHelper.hasRaid(g.getId())) return;

        if(new Random().nextInt(100) < RAID_CHANCE)
        {
            RaidEventHelper.start(g);
            return;
        }

        List<TextChannel> channels = new ServerDataQuery(g.getId()).getSpawnChannels().stream().map(g::getTextChannelById).filter(Objects::nonNull).collect(Collectors.toList());

        if(channels.isEmpty())
        {
            LoggerHelper.warn(SpawnEventHelper.class, g.getName() + " has no Spawn Channels! Skipping spawn event...");
            return;
        }

        spawn = Global.normalCase(spawn);
        boolean shiny = new Random().nextInt(4096) < 1;

        if(LocalDateTime.now(ZoneId.of("America/Los_Angeles")).getHour() == 20)
        {
            if(new Random().nextInt(100) < 1) spawn = PokemonRarity.getLegendarySpawn();
        }

        if(Math.random() < 0.03) new Thread(CommandMarket::addBotEntry).start();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("A wild Pokemon spawned!")
                .setDescription("Try to guess its name and catch it with p!catch <name>!")
                .setColor(new Color(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)))
                .setImage("attachment://pkmn.png");

        try
        {
            String URLString;

            if(spawn.equals("Deerling")) URLString = Global.getDeerlingImage(shiny);
            else if(spawn.equals("Sawsbuck")) URLString = Global.getSawsbuckImage(shiny);
            else URLString = shiny ? DataHelper.pokeData(spawn).shinyURL : DataHelper.pokeData(spawn).normalURL;

            URL url = new URL(URLString.equals("") ? Pokemon.getWIPImage() : URLString);
            BufferedImage img = ImageIO.read(url);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(img, "png", out);

            byte[] bytes = out.toByteArray();

            for(TextChannel c : channels) c.sendFile(bytes, "pkmn.png").setEmbeds(embed.build()).queue();
        }
        catch (Exception e)
        {
            LoggerHelper.error(SpawnEventHelper.class, "Spawn Event failed in " + g.getName() + " (" + g.getId() + ") trying to spawn " + spawn + "!");
            e.printStackTrace();
        }

        LoggerHelper.info(SpawnEventHelper.class, "New Spawn Event – " + g.getName() + " (" + g.getId() + ") - " + spawn + " (Shiny? " + shiny + ")!");
        SERVER_SPAWNS.put(g.getId(), (shiny ? "Shiny " : "") + spawn);
    }

    public static void removeServer(String serverID)
    {
        SCHEDULERS.get(serverID).cancel(true);
        SCHEDULERS.remove(serverID);
        SERVER_SPAWNS.remove(serverID);
    }
}
