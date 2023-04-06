package com.calculusmaster.pokecord.util;

import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.EnumSet;
import java.util.SplittableRandom;

public class Global
{
    public static final EnumSet<PokemonEntity> STARTERS = EnumSet.of(PokemonEntity.BULBASAUR, PokemonEntity.CHARMANDER, PokemonEntity.SQUIRTLE, PokemonEntity.CHIKORITA, PokemonEntity.CYNDAQUIL, PokemonEntity.TOTODILE, PokemonEntity.TREECKO, PokemonEntity.TORCHIC, PokemonEntity.MUDKIP, PokemonEntity.TURTWIG, PokemonEntity.CHIMCHAR, PokemonEntity.PIPLUP, PokemonEntity.SNIVY, PokemonEntity.TEPIG, PokemonEntity.OSHAWOTT, PokemonEntity.CHESPIN, PokemonEntity.FENNEKIN, PokemonEntity.FROAKIE, PokemonEntity.ROWLET, PokemonEntity.LITTEN, PokemonEntity.POPPLIO, PokemonEntity.GROOKEY, PokemonEntity.SCORBUNNY, PokemonEntity.SOBBLE, PokemonEntity.SPRIGATITO, PokemonEntity.FUECOCO, PokemonEntity.QUAXLY);

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
        return Global.timeNow().toEpochSecond(ZoneOffset.UTC);
    }

    public static boolean isStarter(PokemonEntity entity)
    {
        return STARTERS.contains(entity);
    }

    public static int getGeneration(PokemonData data)
    {
        if(data.getDex() <= 151) return 1;
        else if(data.getDex() <= 251) return 2;
        else if(data.getDex() <= 386) return 3;
        else if(data.getDex() <= 493) return 4;
        else if(data.getDex() <= 649) return 5;
        else if(data.getDex() <= 721) return 6;
        else if(data.getDex() <= 809) return 7;
        else if(data.getDex() <= 905) return 8;
        else return 9;
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
