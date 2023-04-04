package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.util.helpers.event.SpawnEventHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.List;
import java.util.Objects;

public class CommandDev extends PokeWorldCommand
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
        else if(command[0].equals("addpokemon"))
        {
            PokemonEntity e = command.length > 1 ? PokemonEntity.cast(command[1]) : PokemonEntity.getRandom();

            Pokemon p = Pokemon.create(e);
            p.upload();

            this.playerData.addPokemon(p.getUUID());

            this.response = "Added a new **" + p.getName() + "** to your Pokemon.";
        }
        else return this.error("Invalid command.");

        return true;
    }
}
