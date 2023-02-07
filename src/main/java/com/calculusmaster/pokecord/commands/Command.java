package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.functional.Tips;
import com.calculusmaster.pokecord.game.player.level.MasteryLevelManager;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.Instant;
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
    protected String response;
    protected Color color;

    public Command(MessageReceivedEvent event, String[] msg, boolean serverData)
    {
        this.event = event;
        this.msg = msg;
        this.mentions = event.getMessage().getMentions().getMembers();

        this.player = event.getAuthor();
        this.server = event.getGuild();

        this.serverData = serverData ? new ServerDataQuery(this.server.getId()) : null;
        this.playerData = PlayerDataQuery.of(this.player.getId());

        this.embed = new EmbedBuilder();
        this.response = "";
        this.color = null;
    }

    public Command(MessageReceivedEvent event, String[] msg)
    {
        this(event, msg, false);
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

    protected Command invalid()
    {
        this.response = CommandInvalid.getShort();
        return this;
    }

    protected void invalidCredits(int req)
    {
        this.response = "Insufficient Credits! Needed: `" + req + "`, you have `" + this.playerData.getCredits() + "`!";
    }

    protected boolean insufficientMasteryLevel(Feature feature)
    {
        return Feature.DISABLED.contains(feature) || (MasteryLevelManager.ACTIVE && this.playerData.getLevel() < feature.getRequiredLevel());
    }

    protected Command invalidMasteryLevel(Feature feature)
    {
        this.response = Feature.DISABLED.contains(feature) ? "This feature has been temporarily disabled!" : "This feature requires **Pokemon Mastery Level " + feature.getRequiredLevel() + "**!";
        return this;
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
        if(this.serverData == null) this.serverData = new ServerDataQuery(this.server.getId());
        return "`" + this.serverData.getPrefix() + command + "`";
    }

    protected void deleteOriginal()
    {
        if(this.event != null) this.event.getMessage().delete().queue();
    }

    //Embed-Related

    public void send()
    {
        if(!this.response.isEmpty())
        {
            //Add Player Mention
            this.response = this.player.getAsMention() + "\n" + this.response;

            if(this.event != null) this.event.getChannel().sendMessage(this.response).queue();
        }
        else if(this.embed != null)
        {
            //Author
            List<String> professors = Arrays.asList("Pokecord", "Oak", "Juniper", "Elm", "Birch", "Rowan", "Sycamore", "Kukui", "Magnolia", "Sonia");
            this.embed.setAuthor("Professor " + professors.get(new Random().nextInt(professors.size())));

            //Color
            this.embed.setColor(this.color == null ? Global.getRandomColor() : this.color);

            //Tip Footer
            if(this.embed.build().getFooter() == null) this.embed.setFooter("Tip: " + Tips.get().tip);

            //Timestamp
            this.embed.setTimestamp(Instant.now());

            //Finalize
            if(this.event != null) this.event.getChannel().sendMessageEmbeds(this.embed.build()).queue();
        }
        //If this.response.isEmpty() && this.embed == null, another class will handle the Embed or Message response
    }
}
