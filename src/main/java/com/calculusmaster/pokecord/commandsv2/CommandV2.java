package com.calculusmaster.pokecord.commandsv2;

import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.player.level.MasteryLevelManager;
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

import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class CommandV2
{
    private CommandData commandData;

    protected User player;
    protected Guild server;
    protected TextChannel channel;

    protected PlayerDataQuery playerData;
    protected ServerDataQuery serverData;

    protected boolean ephemeral;
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
    protected boolean error(String errorMessage)
    {
        this.response = errorMessage;
        this.ephemeral = true;
        return false;
    }

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

    protected void setDefaultResponses()
    {
        this.ephemeral = false;
        this.embed = new EmbedBuilder();
        this.response = "";
    }

    protected void respond(Consumer<String> text, Consumer<MessageEmbed> embed)
    {
        if(!this.response.isEmpty() && (this.embed != null && !this.embed.isEmpty()))
        {
            text.accept(this.response);
            this.channel.sendMessageEmbeds(this.embed.build()).queue();
        }
        else if(!this.response.isEmpty()) text.accept(this.response);
        else if(this.embed != null) embed.accept(this.embed.build());
    }

    protected void respondInvalidMasteryLevel(Feature feature)
    {
        this.response = Feature.DISABLED.contains(feature) ? "This feature has been temporarily disabled!" : "This feature requires **Pokemon Mastery Level " + feature.getRequiredLevel() + "**!";
    }

    //Parsers
    public void parseSlashCommand(SlashCommandInteractionEvent event)
    {
        this.setPlayer(event.getUser());
        this.setServer(Objects.requireNonNull(event.getGuild()));
        this.setChannel(event.getChannel().asTextChannel());

        this.setDefaultResponses();

        boolean result;
        if(this.commandData.hasFeature() && (Feature.DISABLED.contains(this.commandData.getFeature()) || (MasteryLevelManager.ACTIVE && this.playerData.getLevel() < this.commandData.getFeature().getRequiredLevel())))
        {
            this.respondInvalidMasteryLevel(this.commandData.getFeature());
            result = false;
        }
        else result = this.slashCommandLogic(event);

        if(!result && this.embed != null && !this.embed.isEmpty()) this.embed.setColor(Color.RED);
        if(!result && !this.response.isEmpty()) this.response = "[ERROR] " + this.response;

        this.respond(s -> {
            if(event.isAcknowledged()) event.getHook().editOriginal(s).queue();
            else event.reply(s).setEphemeral(this.ephemeral).queue();
        }, e -> {
            if(event.isAcknowledged()) event.getHook().editOriginalEmbeds(e).queue();
            else event.replyEmbeds(e).setEphemeral(this.ephemeral).queue();
        });
    }

    public void parseAutocomplete(CommandAutoCompleteInteractionEvent event)
    {
        this.setPlayer(event.getUser());
        this.setServer(Objects.requireNonNull(event.getGuild()));
        this.setChannel(Objects.requireNonNull(event.getChannel()).asTextChannel());

        this.setDefaultResponses();
        boolean result = this.autocompleteLogic(event);
    }

    public void parseButtonInteraction(ButtonInteractionEvent event)
    {
        this.setPlayer(event.getUser());
        this.setServer(Objects.requireNonNull(event.getGuild()));
        this.setChannel(Objects.requireNonNull(event.getChannel()).asTextChannel());

        this.setDefaultResponses();
        boolean result = this.buttonLogic(event);
        this.respond(s -> event.reply(s).setEphemeral(this.ephemeral).queue(), e -> event.replyEmbeds(e).setEphemeral(this.ephemeral).queue());
    }

    public void parseModalInteraction(ModalInteractionEvent event)
    {
        this.setPlayer(event.getUser());
        this.setServer(Objects.requireNonNull(event.getGuild()));
        this.setChannel(Objects.requireNonNull(event.getChannel()).asTextChannel());

        this.setDefaultResponses();
        boolean result = this.modalLogic(event);
        this.respond(s -> event.reply(s).setEphemeral(this.ephemeral).queue(), e -> event.replyEmbeds(e).setEphemeral(this.ephemeral).queue());
    }

    public void parseStringSelectMenuInteraction(StringSelectInteractionEvent event)
    {
        this.setPlayer(event.getUser());
        this.setServer(Objects.requireNonNull(event.getGuild()));
        this.setChannel(Objects.requireNonNull(event.getChannel()).asTextChannel());

        this.setDefaultResponses();
        boolean result = this.stringSelectLogic(event);
        this.respond(s -> event.reply(s).setEphemeral(this.ephemeral).queue(), e -> event.replyEmbeds(e).setEphemeral(this.ephemeral).queue());
    }

    public void parseEntitySelectMenuInteraction(EntitySelectInteractionEvent event)
    {
        this.setPlayer(event.getUser());
        this.setServer(Objects.requireNonNull(event.getGuild()));
        this.setChannel(Objects.requireNonNull(event.getChannel()).asTextChannel());

        this.setDefaultResponses();
        boolean result = this.entitySelectLogic(event);
        this.respond(s -> event.reply(s).setEphemeral(this.ephemeral).queue(), e -> event.replyEmbeds(e).setEphemeral(this.ephemeral).queue());
    }

    //Overrides (Slash Command is required)
    protected abstract boolean slashCommandLogic(SlashCommandInteractionEvent event);
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event) { return false; }
    protected boolean buttonLogic(ButtonInteractionEvent event) { return false; }
    protected boolean modalLogic(ModalInteractionEvent event) { return false; }
    protected boolean stringSelectLogic(StringSelectInteractionEvent event) { return false; }
    protected boolean entitySelectLogic(EntitySelectInteractionEvent event) { return false; }

    //Misc
    public void setCommandData(CommandData commandData)
    {
        this.commandData = commandData;
    }
}
