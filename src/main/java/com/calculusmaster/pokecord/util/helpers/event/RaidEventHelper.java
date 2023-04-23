package com.calculusmaster.pokecord.util.helpers.event;

import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.game.world.RaidEvent;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity.Rarity.*;

public class RaidEventHelper
{
    private static final Map<String, RaidEvent> RAID_EVENTS = new HashMap<>();

    public static void start(Guild g, TextChannel channel)
    {
        LoggerHelper.info(RaidEventHelper.class, "Creating new Raid in " + g.getName() + " (" + g.getId() + ").");

        PokemonEntity raidPokemon;
        if(new Random().nextFloat() < 0.05F) raidPokemon = PokemonEntity.ETERNATUS_ETERNAMAX;
        else raidPokemon = PokemonRarity.getPokemon(false, DIAMOND, PLATINUM, MYTHICAL, ULTRA_BEAST, LEGENDARY);

        RaidEvent event = new RaidEvent(raidPokemon, channel);

        channel.sendMessageEmbeds(event.createEmbed().build()).queue(m -> event.setMessageID(m.getId()));

        event.queueStart();

        RAID_EVENTS.put(g.getId(), event);
    }

    public static boolean hasRaid(String serverID)
    {
        return RAID_EVENTS.containsKey(serverID);
    }

    public static RaidEvent getRaidEvent(String serverID)
    {
        return RAID_EVENTS.get(serverID);
    }

    public static void removeEvent(String serverID)
    {
        RAID_EVENTS.remove(serverID);
    }

    public static void close()
    {
        RAID_EVENTS.values().forEach(e -> e.getEvent().cancel(true));
        RAID_EVENTS.clear();
    }

}
