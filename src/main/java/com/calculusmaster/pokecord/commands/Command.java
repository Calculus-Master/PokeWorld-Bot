package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.game.enums.functional.Tips;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public abstract class Command
{
    protected MessageReceivedEvent event;
    protected ButtonClickEvent buttonEvent;
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
        this.buttonEvent = null;
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

    public Command(ButtonClickEvent event, String[] msg)
    {
        this.event = null;
        this.buttonEvent = event;
        this.msg = msg;
        this.mentions = new ArrayList<>();

        this.player = event.getMember().getUser();
        this.server = event.getGuild();

        this.serverData = new ServerDataQuery(this.server.getId());
        this.playerData = new PlayerDataQuery(this.player.getId());

        this.embed = new EmbedBuilder();
        this.color = null;

        this.timeI = System.currentTimeMillis();
    }

    public abstract Command runCommand();

    //Useful Methods for other Commands

    protected boolean isNumeric(int index)
    {
        return this.msg[index].chars().allMatch(Character::isDigit);
    }

    protected boolean isPokemon(String pokemon)
    {
        return PokemonData.POKEMON.stream().anyMatch(pokemon::equalsIgnoreCase);
    }

    protected int getInt(int index)
    {
        return Integer.parseInt(this.msg[index]);
    }

    protected void sendMsg(String msg)
    {
        this.embed = null;
        if(this.event != null) this.event.getChannel().sendMessage(this.playerData.getMention() + ": " + msg).queue();
        else if(this.buttonEvent != null)  this.buttonEvent.getChannel().sendMessage(this.playerData.getMention() + ": " + msg).queue();
    }

    protected Command sendDefaultInvalid()
    {
        this.sendMsg(CommandInvalid.getShort());
        return this;
    }

    protected void sendInvalidCredits(int req)
    {
        this.sendMsg("Insufficient Credits! Needed: `" + req + "`, you have `" + this.playerData.getCredits() + "`!");
    }

    protected void sendInvalidLevel(int req, String after)
    {
        this.sendMsg("You must be Pokemon Mastery Level " + req + " " + after + "!");
    }

    protected String getMultiWordContent(int start)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = start; i < this.msg.length; i++) sb.append(this.msg[i]).append(" ");

        return sb.toString().trim();
    }

    protected Member getMember(String ID)
    {
        this.server.retrieveMemberById(ID).queue();
        return this.server.getMemberById(ID);
    }

    protected String getCommandFormatted(String command)
    {
        return "`" + this.serverData.getPrefix() + command + "`";
    }

    protected void deleteOriginal()
    {
        if(this.event != null) this.event.getMessage().delete().queue();
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

    private void setTipFooter()
    {
        if(this.embed.build().getFooter() == null) this.embed.setFooter("Tip: " + Tips.get().tip);
    }

    public MessageEmbed getResponseEmbed()
    {
        this.timeF = System.currentTimeMillis();
        LoggerHelper.time(this.getClass(), this.msg[0], this.timeI, this.timeF);

        this.setAuthor();
        this.setColor();
        this.setTipFooter();
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
