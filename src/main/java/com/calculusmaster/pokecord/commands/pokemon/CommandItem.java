package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.player.PlayerInventory;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Objects;

public class CommandItem extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("item")
                .withConstructor(CommandItem::new)
                .withFeature(Feature.GIVE_POKEMON_ITEMS)
                .withCommand(Commands
                        .slash("item", "Manage items for your Pokemon.")
                        .addSubcommands(
                                new SubcommandData("give", "Give an item to your active Pokemon.")
                                        .addOption(OptionType.STRING, "item", "The name of the item to give to your active Pokemon.", true, true),
                                new SubcommandData("remove", "Remove an item from your active Pokemon.")
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        Pokemon active = this.playerData.getSelectedPokemon();

        if(subcommand.equals("give"))
        {
            OptionMapping itemOption = Objects.requireNonNull(event.getOption("item"));
            String itemInput = itemOption.getAsString();

            Item item = Item.cast(itemInput);
            if(item == null) return this.error("\"" + itemInput + "\" is not a valid item!");
            else if(item.isFunctionalItem()) return this.error("This item cannot be given to a Pokemon.");

            PlayerInventory inv = this.playerData.getInventory();

            if(!inv.hasItem(item)) return this.error("You do not have any " + item.getStyledName() + " in your inventory.");

            if(active.hasItem())
            {
                Item current = active.getItem();

                if(current.equals(item)) return this.error(active.getName() + " is already holding a " + item.getStyledName() + "!");

                inv.addItem(current);
                inv.removeItem(item);

                active.setItem(item);

                this.response = "Replaced " + active.getName() + "'s **" + current.getStyledName() + "** with **" + item.getStyledName() + "**!";
            }
            else
            {
                inv.removeItem(item);

                active.setItem(item);

                this.response = "Gave **" + item.getStyledName() + "** to " + active.getName() + "!";
            }

            this.playerData.updateInventory();
            active.updateItem();
        }
        else if(subcommand.equals("remove"))
        {
            if(!active.hasItem()) return this.error(active.getName() + " is not holding any items.");

            Item i = active.getItem();

            active.removeItem();
            active.updateItem();

            this.playerData.getInventory().addItem(i);
            this.playerData.updateInventory();

            this.response = "Removed **" + i.getStyledName() + "** from " + active.getName() + "!";
        }

        return true;
    }

    @Override
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event)
    {
        if(event.getFocusedOption().getName().equals("item"))
            event.replyChoiceStrings(this.getAutocompleteOptions(event.getFocusedOption().getValue(), this.playerData.getInventory().getItems().keySet().stream().map(Item::getStyledName).toList())).queue();

        return true;
    }
}
