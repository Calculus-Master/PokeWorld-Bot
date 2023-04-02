package com.calculusmaster.pokecord.commandsv2;

import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.commandsv2.economy.CommandBalance;
import com.calculusmaster.pokecord.commandsv2.misc.CommandDev;
import com.calculusmaster.pokecord.commandsv2.move.CommandMoveInfo;
import com.calculusmaster.pokecord.commandsv2.player.CommandStart;
import com.calculusmaster.pokecord.commandsv2.pokemon.CommandCatch;
import com.calculusmaster.pokecord.commandsv2.pokemon.CommandMega;
import com.calculusmaster.pokecord.commandsv2.pokemon.CommandPokeDex;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
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

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommandHandler extends ListenerAdapter
{
    //Command Upsert
    public static void main(String[] args) throws LoginException, InterruptedException
    {
        //Guild Commands
        Pokecord.initializeDiscordBot();
        CommandHandler.init();
    }

    public static final List<CommandData> COMMANDS = new ArrayList<>();

    public static void init()
    {
        CommandStart.init();
        CommandCatch.init();
        CommandPokeDex.init();

        CommandBalance.init();

        CommandMega.init();

        CommandMoveInfo.init();

        CommandDev.init();

        //TODO: Global Commands
        Pokecord.BOT_JDA.getGuildById(Pokecord.TEST_SERVER_ID).updateCommands().addCommands(COMMANDS.stream().map(CommandData::getSlashCommandData).toList()).queue();
    }

    //Listeners
    private CommandData findCommandData(Predicate<CommandData> predicate)
    {
        return COMMANDS.stream().filter(predicate).findFirst().orElse(null);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        CommandData data = this.findCommandData(c -> c.getCommandName().equals(event.getName()));

        if(data == null)
        {
            LoggerHelper.error(CommandHandler.class, "Slash Command not found: " + event.getName());
            event.reply("An error has occurred.").setEphemeral(true).queue();
        }
        else if(!(event.getChannel() instanceof TextChannel))
        {
            LoggerHelper.warn(CommandHandler.class, "Attempted use of Slash Command (%s) used in non-TextChannel (%s, Type: %s)".formatted(event.getName(), event.getChannel().getName(), event.getChannel().getType()));
            event.reply("Slash Command usage outside of standard Text Channels is not currently supported.").setEphemeral(true).queue();
        }
        else
        {
            LoggerHelper.info(CommandHandler.class, "Parsing Slash Command: /" + event.getFullCommandName() + " " + event.getOptions().stream().map(o -> o.getName() + ": " + o.getAsString()).collect(Collectors.joining(" ")));
            data.getInstance().parseSlashCommand(event);
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event)
    {
        CommandData data = this.findCommandData(c -> c.getCommandName().equals(event.getName()));

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
