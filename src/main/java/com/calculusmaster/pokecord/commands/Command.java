package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public abstract class Command
{
    protected MessageReceivedEvent event;
    protected String[] msg;
    protected List<Member> mentions;

    protected User player;
    protected Guild server;

    protected ServerDataQuery serverData;
    protected PlayerDataQuery playerData;

    protected EmbedBuilder embed;
    protected Color color;

    private long timeI, timeF;

    public Command(MessageReceivedEvent event, String[] msg)
    {
        this.event = event;
        this.msg = msg;
        this.mentions = event.getMessage().getMentionedMembers();

        this.player = event.getAuthor();
        this.server = event.getGuild();

        this.serverData = new ServerDataQuery(this.server.getId());
        this.playerData = new PlayerDataQuery(this.player.getId());

        this.embed = new EmbedBuilder();
        this.color = null;

        this.timeI = System.currentTimeMillis();
    }

    public abstract Command runCommand();

    public Commands.Registry getRegistry()
    {
        return Commands.getRegistry(this.msg[0]);
    }

    //Useful Methods for other Commands
    //TODO: Roll these out to all of the commands
    protected boolean isLength(int len)
    {
        return this.msg.length >= len;
    }

    protected boolean isNumeric(int index)
    {
        return this.msg[index].chars().allMatch(Character::isDigit);
    }

    protected boolean isPokemon(String pokemon)
    {
        return Global.POKEMON.contains(Global.normalCase(pokemon));
    }

    protected int getInt(int index)
    {
        return Integer.parseInt(this.msg[index]);
    }

    protected void sendMsg(String msg)
    {
        this.embed = null;
        this.event.getChannel().sendMessage(this.playerData.getMention() + ": " + msg).queue();
    }

    protected void sendInvalidCredits(int req)
    {
        this.sendMsg("Insufficient Credits! Needed: `" + req + "`, you have `" + this.playerData.getCredits() + "`!");
    }

    protected String getMultiWordContent(int start)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = start; i < this.msg.length; i++) sb.append(this.msg[i]).append(" ");

        return sb.toString().trim();
    }

    //Embed-Related

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
        this.timeF = System.currentTimeMillis();
        LoggerHelper.time(this.getClass(), this.msg[0], this.timeI, this.timeF);

        this.setAuthor();
        this.setColor();
        return this.embed.build();
    }

    public boolean isNull()
    {
        return this.embed == null;
    }

    private Color getRandomColor()
    {
        Random r = new Random();
        return new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
    }
}
