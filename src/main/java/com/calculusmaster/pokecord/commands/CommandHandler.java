package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.commands.duel.*;
import com.calculusmaster.pokecord.commands.economy.*;
import com.calculusmaster.pokecord.commands.misc.CommandDev;
import com.calculusmaster.pokecord.commands.misc.CommandWorld;
import com.calculusmaster.pokecord.commands.move.CommandMoves;
import com.calculusmaster.pokecord.commands.move.CommandTM;
import com.calculusmaster.pokecord.commands.player.*;
import com.calculusmaster.pokecord.commands.pokemon.*;
import com.calculusmaster.pokecord.game.pokemon.evolution.CommandForm;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommandHandler extends ListenerAdapter
{
    //Command Upsert
    public static void main(String[] args) throws LoginException, InterruptedException
    {
        //Guild Commands
        Pokeworld.initializeDiscordBot();

        //TODO: Global Commands
        CommandHandler.updateGuildCommands();
    }

    //HashMap used for Slash Commands for optimization, other Interaction Entities use the List since it needs to be iterated through
    public static final List<CommandData> COMMANDS = new ArrayList<>();
    public static final Map<String, CommandData> COMMAND_DATA = new HashMap<>();

    public static void init()
    {
        CommandStart.init();
        CommandBugReport.init();
        CommandAchievements.init();
        CommandLevel.init();
        CommandProfile.init();
        CommandLeaderboard.init();
        CommandTasks.init();

        CommandCatch.init();
        CommandPokeDex.init();
        CommandSelect.init();
        CommandMega.init();
        CommandEvolve.init();
        CommandRelease.init();
        CommandTeam.init();
        CommandItem.init();
        CommandNickname.init();
        CommandInfo.init();
        CommandFavorites.init();
        CommandZCrystal.init();
        CommandAugments.init();
        CommandPrestige.init();
        CommandForm.init();
        CommandEggs.init();

        CommandUse.init();
        CommandDuel.init();
        CommandTrainerDuel.init();
        CommandEliteDuel.init();
        CommandWildDuel.init();

        CommandMoves.init();
        CommandTM.init();

        CommandBalance.init();
        CommandInventory.init();
        CommandShop.init();
        CommandTrade.init();
        CommandRedeem.init();

        CommandWorld.init();

        CommandDev.init();

        LoggerHelper.info(CommandHandler.class, "Loaded %d Interaction Commands.".formatted(COMMANDS.size()));

        CommandHandler.updateGuildCommands(); //TODO: Remove this once Global Commands are implemented
    }

    public static void updateGuildCommands()
    {
        Guild testServer = Pokeworld.BOT_JDA.getGuildById(Pokeworld.TEST_SERVER_ID);

        List<String> registeredCommands = new ArrayList<>();
        testServer.retrieveCommands().onSuccess(l -> registeredCommands.addAll(l.stream().map(Command::getName).toList())).queue();

        List<CommandData> commandsToAdd = new ArrayList<>();
        for(CommandData d : COMMANDS) if(!registeredCommands.contains(d.getCommandName())) commandsToAdd.add(d);
        testServer.updateCommands().addCommands(commandsToAdd.stream().map(CommandData::getSlashCommandData).toList()).queue();
    }

    //Listeners
    private CommandData findCommandData(Predicate<CommandData> predicate)
    {
        return COMMANDS.stream().filter(predicate).findFirst().orElse(null);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        CommandData data = COMMAND_DATA.get(event.getName());

        if(data == null)
        {
            LoggerHelper.error(CommandHandler.class, "Slash Command not found: " + event.getName());
            event.reply("An error has occurred.").setEphemeral(true).queue();
        }
        else if(!event.isFromGuild())
        {
            LoggerHelper.warn(CommandHandler.class, "Attempted use of Slash Command (%s) outside of a Guild (%s)".formatted(event.getName(), event.getChannel().getName()));
            event.reply("Slash Command usage outside of a Guild is not currently supported.").setEphemeral(true).queue();
        }
        else if(!(event.getChannel() instanceof TextChannel))
        {
            LoggerHelper.warn(CommandHandler.class, "Attempted use of Slash Command (%s) used in non-TextChannel (%s, Type: %s)".formatted(event.getName(), event.getChannel().getName(), event.getChannel().getType()));
            event.reply("Slash Command usage outside of standard Text Channels is not currently supported.").setEphemeral(true).queue();
        }
        else if(!data.getCommandName().equals("start") && !PlayerDataQuery.isRegistered(event.getUser().getId()))
            event.reply("You have not started your journey with " + Pokeworld.NAME + " yet! Use the `/start` command to begin.").setEphemeral(true).queue();
        else
        {
            LoggerHelper.info(CommandHandler.class, "Parsing Slash Command: /" + event.getFullCommandName() + " " + event.getOptions().stream().map(o -> o.getName() + ": " + o.getAsString()).collect(Collectors.joining(" ")));
            data.getInstance().parseSlashCommand(event);
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event)
    {
        CommandData data = COMMAND_DATA.get(event.getName());

        if(data == null) LoggerHelper.error(CommandHandler.class, "Autocomplete Slash Command not found: " + event.getName());
        else data.getInstance().parseAutocomplete(event);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event)
    {
        CommandData data = this.findCommandData(c -> c.hasButton(event.getComponentId()));

        if(data == null)
        {
            LoggerHelper.error(CommandHandler.class, "Button ID not found: " + event.getComponentId());
            event.reply("An error has occurred.").setEphemeral(true).queue();
        }
        else data.getInstance().parseButtonInteraction(event);
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event)
    {
        CommandData data = this.findCommandData(c -> c.hasModal(event.getModalId()));

        if(data == null)
        {
            LoggerHelper.error(CommandHandler.class, "Modal ID not found: " + event.getModalId());
            event.reply("An error has occurred.").setEphemeral(true).queue();
        }
        else data.getInstance().parseModalInteraction(event);
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event)
    {
        CommandData data = this.findCommandData(c -> c.hasStringSelect(event.getComponentId()));

        if(data == null)
        {
            LoggerHelper.error(CommandHandler.class, "String Select ID not found: " + event.getComponentId());
            event.reply("An error has occurred.").setEphemeral(true).queue();
        }
        else data.getInstance().parseStringSelectMenuInteraction(event);
    }

    @Override
    public void onEntitySelectInteraction(EntitySelectInteractionEvent event)
    {
        CommandData data = this.findCommandData(c -> c.hasEntitySelect(event.getComponentId()));

        if(data == null)
        {
            LoggerHelper.error(CommandHandler.class, "Entity Select ID not found: " + event.getComponentId());
            event.reply("An error has occurred.").setEphemeral(true).queue();
        }
        else data.getInstance().parseEntitySelectMenuInteraction(event);
    }

    //Context Commands

    @Override
    public void onMessageContextInteraction(MessageContextInteractionEvent event)
    {
        //TODO: Message Context Interactions
    }

    @Override
    public void onUserContextInteraction(UserContextInteractionEvent event)
    {
        //TODO: User Context Interactions
    }
}
