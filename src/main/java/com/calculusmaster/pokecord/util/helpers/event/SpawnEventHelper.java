package com.calculusmaster.pokecord.util.helpers.event;

import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.component.CustomPokemonData;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SpawnEventHelper
{
    public static int RAID_CHANCE;
    public static int SPAWN_INTERVAL;

    private static final Map<String, SpawnData> SERVER_SPAWNS = new HashMap<>();
    private static final Map<String, ScheduledFuture<?>> SCHEDULERS = new HashMap<>();

    public static List<String> getSnapshot()
    {
        List<String> out = new ArrayList<>();
        out.add("Count: " + SCHEDULERS.size());

        SCHEDULERS.keySet().forEach(serverID -> {
            SpawnData d = SERVER_SPAWNS.get(serverID);

            long delay = SCHEDULERS.get(serverID).getDelay(TimeUnit.SECONDS);
            int min = (int)delay / 60;
            int sec = (int)delay % 60;

            String line = "Server: %s | Spawn: %s %s | T-%s".formatted(serverID, d == null ? "None" : d.getSpawn().getName(), d == null || !d.isShiny() ? "" : " (Shiny)", min + "m" + sec + "s");
            out.add(line);
        });

        return out;
    }

    public static void start(Guild g)
    {
        start(g, SPAWN_INTERVAL);
    }

    private static void start(Guild g, int interval)
    {
        ScheduledFuture<?> spawnEvent = ThreadPoolHandler.SPAWN.scheduleWithFixedDelay(() -> spawnPokemon(g), interval, interval, TimeUnit.SECONDS);

        SCHEDULERS.put(g.getId(), spawnEvent);
    }

    public static SpawnData getSpawn(String serverID)
    {
        return SERVER_SPAWNS.getOrDefault(serverID, null);
    }

    public static void clearSpawn(String serverID)
    {
        SERVER_SPAWNS.put(serverID, null);
    }

    private static void spawnPokemon(Guild g)
    {
        SpawnEventHelper.spawnPokemon(g, PokemonRarity.getSpawn());
    }

    public static void forceSpawn(Guild g, PokemonEntity spawn)
    {
        SpawnEventHelper.removeServer(g.getId());

        SpawnEventHelper.spawnPokemon(g, spawn);

        SpawnEventHelper.start(g);
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

    private static void spawnPokemon(Guild g, PokemonEntity spawn)
    {
        List<TextChannel> channels = new ServerDataQuery(g.getId()).getSpawnChannels().stream().map(g::getTextChannelById).filter(Objects::nonNull).toList();
        final Random random = new Random();

        //Check if spawn channels are available
        if(channels.isEmpty())
        {
            LoggerHelper.info(SpawnEventHelper.class, "Skipped Spawn Event! Reason: No Spawn Channels | Server: %s (%s)".formatted(g.getName(), g.getId()));
            return;
        }
        //No spawns during Raids
        else if(RaidEventHelper.hasRaid(g.getId()))
        {
            LoggerHelper.info(SpawnEventHelper.class, "Skipped Spawn Event! Reason: Active Raid | Server: %s (%s)".formatted(g.getName(), g.getId()));
            return;
        }
        //Random chance to convert this spawn into a Raid Event
        else if(random.nextInt(100) < RAID_CHANCE)
        {
            RaidEventHelper.start(g, channels.get(0));
            LoggerHelper.info(SpawnEventHelper.class, "New Raid Event! Server: %s (%s)".formatted(g.getName(), g.getId()));
            return;
        }

        //Shiny odds
        boolean shiny = random.nextInt(4096) < 1;

        //Custom data
        CustomPokemonData customData = new CustomPokemonData().generateOnSpawn(spawn);

        //Legendary Hour
        if(Global.timeNow().getHour() == 20 && random.nextInt(100) < 1)
            spawn = PokemonRarity.getLegendarySpawn();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("A wild Pokemon spawned!")
                .setDescription("*Try to catch it with* `/catch`!")
                .setColor(shiny ? new Color(255, 249, 194) : spawn.data().getTypes().get(0).getColor())
                .setImage("attachment://pkmn.png");

        try
        {
            String subpath = Pokemon.getImage(spawn, shiny, null, null);
            URI resourcesURI = SpawnEventHelper.class.getResource(subpath).toURI();

            BufferedImage img = ImageIO.read(resourcesURI.toURL()); //.getScaledInstance(300, 300, hint)
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(img, "png", out);

            byte[] bytes = out.toByteArray();

            for(TextChannel c : channels) c.sendFiles(FileUpload.fromData(bytes, "pkmn.png")).setEmbeds(embed.build()).queue();
        }
        catch (IOException | URISyntaxException | NullPointerException e)
        {
            LoggerHelper.reportError(SpawnEventHelper.class, "Spawn Event failed in " + g.getName() + " (" + g.getId() + ") trying to spawn " + spawn + "!", e);
        }

        LoggerHelper.info(SpawnEventHelper.class, "New Spawn Event – " + g.getName() + " (" + g.getId() + ") - " + spawn + " (Shiny? " + shiny + ")!");
        SERVER_SPAWNS.put(g.getId(), new SpawnData(spawn, shiny, customData));
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

    public static class SpawnData
    {
        private final PokemonEntity spawn;
        private final boolean shiny;
        private final CustomPokemonData customData;

        SpawnData(PokemonEntity spawn, boolean shiny, CustomPokemonData customData)
        {
            this.spawn = spawn;
            this.shiny = shiny;
            this.customData = customData;
        }

        public boolean isShiny()
        {
            return this.shiny;
        }

        public PokemonEntity getSpawn()
        {
            return spawn;
        }

        public CustomPokemonData getCustomData()
        {
            return customData;
        }
    }
}
