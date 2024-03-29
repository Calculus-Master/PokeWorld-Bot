package com.calculusmaster.pokecord.util;

import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.SplittableRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Global
{
    //Global thread pool for random one-shot tasks
    public static final ExecutorService GLOBAL = Executors.newVirtualThreadPerTaskExecutor();

    public static final int MAX_NAME_LIMIT = 40;
    public static final EnumSet<PokemonEntity> STARTERS = EnumSet.of(PokemonEntity.BULBASAUR, PokemonEntity.CHARMANDER, PokemonEntity.SQUIRTLE, PokemonEntity.CHIKORITA, PokemonEntity.CYNDAQUIL, PokemonEntity.TOTODILE, PokemonEntity.TREECKO, PokemonEntity.TORCHIC, PokemonEntity.MUDKIP, PokemonEntity.TURTWIG, PokemonEntity.CHIMCHAR, PokemonEntity.PIPLUP, PokemonEntity.SNIVY, PokemonEntity.TEPIG, PokemonEntity.OSHAWOTT, PokemonEntity.CHESPIN, PokemonEntity.FENNEKIN, PokemonEntity.FROAKIE, PokemonEntity.ROWLET, PokemonEntity.LITTEN, PokemonEntity.POPPLIO, PokemonEntity.GROOKEY, PokemonEntity.SCORBUNNY, PokemonEntity.SOBBLE, PokemonEntity.SPRIGATITO, PokemonEntity.FUECOCO, PokemonEntity.QUAXLY);
    public static final EnumSet<PokemonEntity> DYNAMAX_BAN_LIST = EnumSet.of(PokemonEntity.ZACIAN, PokemonEntity.ZAMAZENTA, PokemonEntity.ETERNATUS, PokemonEntity.NECROZMA_ULTRA);

    public static boolean userHasAdmin(Guild server, User player)
    {
        server.retrieveMemberById(player.getId());

        return server.getMemberById(player.getId()).hasPermission(Permission.ADMINISTRATOR);
    }

    public static int clamp(int val, int min, int max)
    {
        return val < min ? min : (val > max ? max : val);
    }

    //Returns the current moment in time, in PST
    public static LocalDateTime timeNow()
    {
        return LocalDateTime.now(ZoneId.of("America/Los_Angeles"));
    }

    public static long timeNowEpoch()
    {
        return Instant.now().getEpochSecond();
    }

    public static boolean isStarter(PokemonEntity entity)
    {
        return STARTERS.contains(entity);
    }

    public static <E extends Enum<E>> E getEnumFromString(E[] enumValues, String s)
    {
        for(E e : enumValues) if(s.equalsIgnoreCase(e.toString())) return e;
        return null;
    }

    public static Color getRandomColor()
    {
        final SplittableRandom random = new SplittableRandom();
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public static String normalize(String s)
    {
        StringBuilder sb = new StringBuilder();
        for(String str : s.replaceAll("-", " ").split("\\s+")) if(!str.isEmpty()) sb.append(str.substring(0, 1).toUpperCase()).append(str.substring(1).toLowerCase()).append(" ");
        return s.contains("-") ? sb.toString().replaceAll("\\s", "-").trim() : sb.toString().trim();
    }
}
