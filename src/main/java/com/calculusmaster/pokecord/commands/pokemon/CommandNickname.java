package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Objects;

public class CommandNickname extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("nickname")
                .withConstructor(CommandNickname::new)
                .withCommand(Commands
                        .slash("nickname", "Create nicknames for your Pokemon!")
                        .addSubcommands(
                                new SubcommandData("set", "Give your active Pokemon a nickname.")
                                        .addOption(OptionType.STRING, "name", "Nickname you want to give.", true),
                                new SubcommandData("clear", "Remove the nickname from your active Pokemon.")
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        Pokemon active = this.playerData.getSelectedPokemon();

        if(subcommand.equals("set"))
        {
            OptionMapping nameOption = Objects.requireNonNull(event.getOption("name"));
            String nameInput = nameOption.getAsString();

            if(nameInput.length() < 3 || nameInput.length() > 40) return this.error("Nickname must be between 3 and 40 characters long.");

            active.setNickname(nameInput);
            active.updateNickname();

            this.response = active.getName() + "'s nickname is now: \"" + nameInput + "\"!";
        }
        else if(subcommand.equals("clear"))
        {
            if(!active.hasNickname()) return this.error(active.getName() + " does not have a nickname.");

            active.setNickname("");
            active.updateNickname();

            this.response = "Removed " + active.getName() + "'s nickname!";
        }

        return true;
    }
}
