package com.calculusmaster.pokecord.commandsv2;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class CommandV2
{
    protected User player;
    protected Guild server;
    protected TextChannel channel;

    protected PlayerDataQuery playerData;
    protected ServerDataQuery serverData;

    protected EmbedBuilder embed;
    protected String response;

    public CommandV2()
    {
        this.player = null;
        this.server = null;
        this.channel = null;

        this.playerData = null;
        this.serverData = null;
    }

    //For Subclasses


    //Internal
    protected void setPlayer(User player)
    {
        this.player = player;
        this.playerData = PlayerDataQuery.of(player.getId());
    }

    protected void setServer(Guild server)
    {
        this.server = server;
        this.serverData = new ServerDataQuery(server.getId()); //TODO: Caching Server Data
    }

    protected void setChannel(TextChannel channel)
    {
        this.channel = channel;
    }

    protected void initResponses()
    {
        this.embed = new EmbedBuilder();
        this.response = "";
    }

    protected void respond(Consumer<String> text, Consumer<MessageEmbed> embed)
    {
        if(!this.response.isEmpty() && this.embed != null)
        {
            text.accept(this.response);
            this.channel.sendMessageEmbeds(this.embed.build()).queue();
        }
        else if(!this.response.isEmpty()) text.accept(this.response);
        else if(this.embed != null) embed.accept(this.embed.build());
    }

    //Parsers
    public void parseSlashCommand(SlashCommandInteractionEvent event)
    {
        this.setPlayer(event.getUser());
        this.setServer(Objects.requireNonNull(event.getGuild()));
        this.setChannel(event.getChannel().asTextChannel());

        this.initResponses();
        boolean result = this.slashCommandLogic(event);
        this.respond(s -> event.reply(s).queue(), e -> event.replyEmbeds(e).queue());
    }

    public void parseAutocomplete(CommandAutoCompleteInteractionEvent event)
    {
        this.setPlayer(event.getUser());
        this.setServer(Objects.requireNonNull(event.getGuild()));
        this.setChannel(Objects.requireNonNull(event.getChannel()).asTextChannel());

        this.initResponses();
        boolean result = this.autocompleteLogic(event);
    }

    public void parseButtonInteraction(ButtonInteractionEvent event)
    {
        this.setPlayer(event.getUser());
        this.setServer(Objects.requireNonNull(event.getGuild()));
        this.setChannel(Objects.requireNonNull(event.getChannel()).asTextChannel());

        this.initResponses();
        boolean result = this.buttonLogic(event);
        this.respond(s -> event.reply(s).queue(), e -> event.replyEmbeds(e).queue());
    }

    public void parseModalInteraction(ModalInteractionEvent event)
    {
        this.setPlayer(event.getUser());
        this.setServer(Objects.requireNonNull(event.getGuild()));
        this.setChannel(Objects.requireNonNull(event.getChannel()).asTextChannel());

        this.initResponses();
        boolean result = this.modalLogic(event);
        this.respond(s -> event.reply(s).queue(), e -> event.replyEmbeds(e).queue());
    }

    public void parseStringSelectMenuInteraction(StringSelectInteractionEvent event)
    {
        this.setPlayer(event.getUser());
        this.setServer(Objects.requireNonNull(event.getGuild()));
        this.setChannel(Objects.requireNonNull(event.getChannel()).asTextChannel());

        this.initResponses();
        boolean result = this.stringSelectLogic(event);
        this.respond(s -> event.reply(s).queue(), e -> event.replyEmbeds(e).queue());
    }

    public void parseEntitySelectMenuInteraction(EntitySelectInteractionEvent event)
    {
        this.setPlayer(event.getUser());
        this.setServer(Objects.requireNonNull(event.getGuild()));
        this.setChannel(Objects.requireNonNull(event.getChannel()).asTextChannel());

        this.initResponses();
        boolean result = this.entitySelectLogic(event);
        this.respond(s -> event.reply(s).queue(), e -> event.replyEmbeds(e).queue());
    }

    //Overrides
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event) { return false; }
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event) { return false; }
    protected boolean buttonLogic(ButtonInteractionEvent event) { return false; }
    protected boolean modalLogic(ModalInteractionEvent event) { return false; }
    protected boolean stringSelectLogic(StringSelectInteractionEvent event) { return false; }
    protected boolean entitySelectLogic(EntitySelectInteractionEvent event) { return false; }
}
