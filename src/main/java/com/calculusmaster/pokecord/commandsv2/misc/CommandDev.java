package com.calculusmaster.pokecord.commandsv2.misc;

import com.calculusmaster.pokecord.commandsv2.CommandData;
import com.calculusmaster.pokecord.commandsv2.CommandV2;
import com.calculusmaster.pokecord.util.helpers.event.SpawnEventHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.List;
import java.util.Objects;

public class CommandDev extends CommandV2
{
    public static void init()
    {
        CommandData
                .create("dev")
                .withConstructor(CommandDev::new)
                .withCommand(Commands
                        .slash("dev", "Secret developer powers.")
                        .addOption(OptionType.STRING, "command", "Developer command to run.", true, false)
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        OptionMapping commandOption = Objects.requireNonNull(event.getOption("command"));

        String[] command = commandOption.getAsString().split("-");

        //Return all the current active Spawn Event Helper timers, the server they're for, and their delay
        if(command[0].equals("getspawntimers"))
        {
            List<String> spawnTimers = SpawnEventHelper.getSnapshot();

            this.response = "SpawnEventHelper Snapshot:\n\n" + String.join("\n - ", spawnTimers);
        }
        else return this.error("Invalid command.");

        return true;
    }
}
