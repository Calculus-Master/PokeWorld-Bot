package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Arrays;
import java.util.Objects;

public class CommandZCrystal extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("zcrystal")
                .withConstructor(CommandZCrystal::new)
                .withFeature(Feature.EQUIP_Z_CRYSTALS)
                .withCommand(Commands
                        .slash("zcrystal", "Equip Z-Crystals to use Z-Moves in Duels!")
                        .addSubcommands(
                                new SubcommandData("equip", "Equip a Z-Crystal.")
                                        .addOption(OptionType.STRING, "name", "The name of the Z-Crystal to equip.", true, true)
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        if(subcommand.equals("equip"))
        {
            OptionMapping nameOption = Objects.requireNonNull(event.getOption("name"));
            String nameInput = nameOption.getAsString();

            ZCrystal z = ZCrystal.cast(nameInput);
            if(z == null) return this.error("\"" + nameInput + "\" is not a valid Z-Crystal name!");
            else if(!this.playerData.getInventory().getZCrystals().contains(z)) return this.error("You do not own the Z-Crystal " + z.getName() + ".");
            else if(DuelHelper.isInDuel(this.player.getId())) return this.error("You cannot equip Z-Crystals while in a Duel.");

            this.playerData.getInventory().setEquippedZCrystal(z);

            this.response = "You've successfully equipped the Z-Crystal **" + z.getName() + "**.";
        }

        return true;
    }

    @Override
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event)
    {
        if(event.getFocusedOption().getName().equals("name"))
            event.replyChoiceStrings(this.getAutocompleteOptions(event.getFocusedOption().getValue(), Arrays.stream(ZCrystal.values()).map(ZCrystal::getName).toList())).queue();

        return true;
    }
}
