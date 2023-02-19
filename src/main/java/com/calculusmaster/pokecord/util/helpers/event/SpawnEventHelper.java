package com.calculusmaster.pokecord.util.helpers.event;

import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.calculusmaster.pokecord.util.helpers.ThreadPoolHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        start(g, SPAWN_INTERVAL);
    }

    private static void start(Guild g, int interval)
    {
        ScheduledFuture<?> spawnEvent = ThreadPoolHandler.SPAWN.scheduleWithFixedDelay(() -> spawnPokemon(g), interval, interval, TimeUnit.SECONDS);

        SCHEDULERS.put(g.getId(), spawnEvent);
    }

    public static String getSpawn(String id)
    {
        return SERVER_SPAWNS.getOrDefault(id, "");
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

        start(g);
    }

    public static void updateSpawnRate(Guild g, int messages)
    {
        if(g == null)
        {
            LoggerHelper.warn(SpawnEventHelper.class, "Failed to change Spawn Rate!");
            return;
        }

        removeServer(g.getId());

        double interval = SPAWN_INTERVAL;

        if(messages <= 10) interval *= 3;
        else if(messages <= 20) interval *= 2;
        else if(messages <= 50) interval *= 1.25;
        else if(messages <= 75) interval *= 1.0;
        else if(messages <= 100) interval *= 0.75;
        else if(messages <= 200) interval *= 0.5;
        else interval *= 0.25;

        start(g, (int)interval);

        LoggerHelper.info(SpawnEventHelper.class, "Updating Spawn Rate in " + g.getName() + "! New Interval: " + (int)(interval) + "s!");
    }

    private static void spawnPokemon(Guild g, String spawn)
    {
        List<TextChannel> channels = new ServerDataQuery(g.getId()).getSpawnChannels().stream().map(g::getTextChannelById).filter(Objects::nonNull).collect(Collectors.toList());
        final SplittableRandom random = new SplittableRandom();

        if(channels.isEmpty())
        {
            LoggerHelper.warn(SpawnEventHelper.class, g.getName() + " has no Spawn Channels! Skipping spawn event...");
            return;
        }

        if(RaidEventHelper.hasRaid(g.getId())) return;

        if(random.nextInt(100) < RAID_CHANCE)
        {
            RaidEventHelper.start(g, channels.get(0));
            return;
        }

        spawn = Global.normalize(spawn);
        boolean shiny = random.nextInt(4096) < 1;

        if(LocalDateTime.now(ZoneId.of("America/Los_Angeles")).getHour() == 20 && random.nextInt(100) < 1)
        {
            spawn = PokemonRarity.getLegendarySpawn();
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("A wild Pokemon spawned!")
                .setDescription("Try to guess its name and catch it with p!catch <name>!")
                .setColor(Global.getRandomColor())
                .setImage("attachment://pkmn.png");

        try
        {
            String URLString;

            if(spawn.equals("Deerling")) URLString = Global.getDeerlingImage(shiny);
            else if(spawn.equals("Sawsbuck")) URLString = Global.getSawsbuckImage(shiny);
            else URLString = shiny ? PokemonData.get(spawn).shinyURL : PokemonData.get(spawn).normalURL;

            URL url = new URL(URLString.equals("") ? Pokemon.getWIPImage() : URLString);
            BufferedImage img = ImageIO.read(url);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(img, "png", out);

            byte[] bytes = out.toByteArray();

            for(TextChannel c : channels) c.sendFiles(FileUpload.fromData(bytes, "pkmn.png")).setEmbeds(embed.build()).queue();
        }
        catch (IOException e)
        {
            LoggerHelper.reportError(SpawnEventHelper.class, "Spawn Event failed in " + g.getName() + " (" + g.getId() + ") trying to spawn " + spawn + "!", e);
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

    public static void close()
    {
        SERVER_SPAWNS.clear();
        SCHEDULERS.values().forEach(future -> future.cancel(true));
        SCHEDULERS.clear();
    }
}
