package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public abstract class Command
{
    protected MessageReceivedEvent event;
    protected String[] msg;
    private final String format;

    protected User player;
    protected Guild server;

    protected ServerDataQuery serverData;
    protected PlayerDataQuery playerData;

    protected EmbedBuilder embed;
    protected Color color;

    public static final List<String> CMD_START = Arrays.asList("start");
    public static final List<String> CMD_SPAWNCHANNEL = Arrays.asList("setspawnchannel");
    public static final List<String> CMD_BALANCE = Arrays.asList("bal", "credits", "c");
    public static final List<String> CMD_SELECT = Arrays.asList("select", "pick");
    public static final List<String> CMD_DEX = Arrays.asList("dex", "pokedex");
    public static final List<String> CMD_INFO = Arrays.asList("info");
    public static final List<String> CMD_CATCH = Arrays.asList("catch");
    public static final List<String> CMD_POKEMON = Arrays.asList("pokemon", "pkmn");
    public static final List<String> CMD_MOVES = Arrays.asList("moves", "m");
    public static final List<String> CMD_MOVEINFO = Arrays.asList("moveinfo", "mi");
    public static final List<String> CMD_LEARN = Arrays.asList("learn", "teach");
    public static final List<String> CMD_REPLACE = Arrays.asList("replace");
    public static final List<String> CMD_DUEL = Arrays.asList("duel");
    public static final List<String> CMD_USE = Arrays.asList("use", "u");

    public Command(MessageReceivedEvent event, String[] msg, String format)
    {
        this.event = event;
        this.msg = msg;
        this.format = format;

        this.player = event.getAuthor();
        this.server = event.getGuild();

        this.serverData = new ServerDataQuery(this.server.getId());
        this.playerData = new PlayerDataQuery(this.player.getId());

        this.embed = new EmbedBuilder();
        this.color = null;
    }

    public abstract Command runCommand() throws IOException;

    protected void setAuthor()
    {
        List<String> professors = Arrays.asList("Pokecord", "Oak", "Juniper", "Elm", "Birch", "Rowan", "Sycamore", "Kukui", "Magnolia", "Sonia");
        this.embed.setAuthor("Professor " + professors.get(new Random().nextInt(professors.size())));
    }

    private void setColor()
    {
        this.embed.setColor(this.color == null ? this.getRandomColor() : this.color);
    }

    public MessageEmbed getResponseEmbed()
    {
        if(this.embed == null) return null;
        this.setAuthor();
        this.setColor();
        return this.embed.build();
    }

    private Color getRandomColor()
    {
        Random r = new Random();
        return new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
    }

    public String getFormat()
    {
        return "`" + this.serverData.getPrefix() + this.format + "`";
    }
}
