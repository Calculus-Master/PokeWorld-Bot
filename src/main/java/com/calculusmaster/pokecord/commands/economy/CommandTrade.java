package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.trade.Trade;
import com.calculusmaster.pokecord.game.trade.elements.TradeOffer;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.Objects;

public class CommandTrade extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("trade")
                .withFeature(Feature.TRADE)
                .withConstructor(CommandTrade::new)
                .withCommand(Commands
                        .slash("trade", "Trade with other players!")
                        .addSubcommands(
                                new SubcommandData("create", "Start a trade with another player.")
                                        .addOption(OptionType.USER, "player", "The player to initiate a trade with.", true),
                                new SubcommandData("accept", "Accept a trade request from another player."),
                                new SubcommandData("deny", "Deny a trade request from another player."),
                                new SubcommandData("confirm", "Confirm a trade. Once both players confirm, the trade will be completed.")
                        )
                        .addSubcommandGroups(
                                new SubcommandGroupData("credits", "Add or remove credits from a trade.")
                                        .addSubcommands(
                                                new SubcommandData("add", "Add credits to a trade.")
                                                        .addOption(OptionType.INTEGER, "amount", "The amount to add.", true),
                                                new SubcommandData("remove", "Remove credits from a trade.")
                                                        .addOption(OptionType.INTEGER, "amount", "Optional: The amount to remove. If not provided, all credits will be removed.", false)
                                        ),
                                new SubcommandGroupData("redeems", "Add or remove redeems from a trade.")
                                        .addSubcommands(
                                                new SubcommandData("add", "Add redeems to a trade.")
                                                        .addOption(OptionType.INTEGER, "amount", "The amount to add.", true),
                                                new SubcommandData("remove", "Remove redeems from a trade.")
                                                        .addOption(OptionType.INTEGER, "amount", "The amount to remove.", false)
                                        ),
                                new SubcommandGroupData("pokemon", "Add or remove Pokemon from a trade.")
                                        .addSubcommands(
                                                new SubcommandData("add", "Add Pokemon to a trade.")
                                                        .addOption(OptionType.INTEGER, "number", "The number of the Pokemon to add.", true),
                                                new SubcommandData("remove", "Remove Pokemon from a trade.")
                                                        .addOption(OptionType.INTEGER, "number", "Optional: The number of the Pokemon to remove. If not provided, all Pokemon will be removed.", false)
                                        ),
                                new SubcommandGroupData("items", "Add or remove items from a trade.")
                                        .addSubcommands(
                                                new SubcommandData("add", "Add items to a trade.")
                                                        .addOption(OptionType.STRING, "name", "The name of the item to add.", true)
                                                        .addOption(OptionType.INTEGER, "amount", "Optional: The amount to add (default: 1).", false),
                                                new SubcommandData("remove", "Remove items from a trade.")
                                                        .addOption(OptionType.STRING, "name", "The name of the item to remove.", true)
                                                        .addOption(OptionType.INTEGER, "amount", "Optional: The amount to remove. If not provided, all items will be removed.", false)
                                        ),
                                new SubcommandGroupData("tms", "Add or remove TMs from a trade.")
                                        .addSubcommands(
                                                new SubcommandData("add", "Add TMs to a trade.")
                                                        .addOption(OptionType.STRING, "name", "The name of the TM to add.", true)
                                                        .addOption(OptionType.INTEGER, "amount", "Optional: The amount to add (default: 1).", false),
                                                new SubcommandData("remove", "Remove TMs from a trade.")
                                                        .addOption(OptionType.STRING, "name", "The name of the TM to remove.", true)
                                                        .addOption(OptionType.INTEGER, "amount", "Optional: The amount to remove. If not provided, all TMs will be removed.", false)
                                        )
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        //Higher Level Trade Commands
        if(event.getSubcommandGroup() == null)
        {
            if(subcommand.equals("create"))
            {
                OptionMapping playerOption = Objects.requireNonNull(event.getOption("player"));
                User player = playerOption.getAsUser();

                if(player.getId().equals(this.player.getId())) return this.error("You cannot trade with yourself.");
                else if(Trade.isInTrade(this.player.getId())) return this.error("You are already in a trade. Complete your current trade to start another.");
                else if(Trade.isInTrade(player.getId())) return this.error(player.getName() + " is already in a trade.");
                else if(!PlayerDataQuery.isRegistered(player.getId())) return this.error(player.getName() + " has not started their journey with " + Pokeworld.NAME + ".");

                Trade.create(this.player.getId(), player.getId(), event.getChannel().asTextChannel());

                this.response = player.getAsMention() + ": " + this.player.getName() + " has invited you to trade! Use `/trade accept` to accept the trade request, or `/trade deny` to deny the request.";
            }
            else if(subcommand.equals("accept"))
            {
                if(!Trade.isInTrade(this.player.getId())) return this.error("You have no active trade request.");

                Trade t = Trade.getTrade(this.player.getId());
                if(!t.isWaiting()) return this.error("You are already in an active trade.");

                event.reply(t.getPlayers()[0].getPlayerData().getMention() + " " + t.getPlayers()[1].getPlayerData().getMention() + ": Trade starting!").queue(ih -> t.start());

                this.setResponsesHandled();
            }
            else if(subcommand.equals("deny"))
            {
                if(!Trade.isInTrade(this.player.getId())) return this.error("You have no active trade request.");

                Trade t = Trade.getTrade(this.player.getId());
                if(!t.isWaiting()) return this.error("You are already in an active trade.");

                Trade.delete(this.player.getId());

                this.response = t.getOther(this.player.getId()).getPlayerData().getMention() + ": " + this.player.getName() + " has denied your trade request.";
            }
            else if(subcommand.equals("confirm"))
            {
                if(!Trade.isInTrade(this.player.getId()) || Trade.getTrade(this.player.getId()).isWaiting()) return this.error("You are not in a trade.");

                Trade t = Trade.getTrade(this.player.getId());

                if(t.isEmpty()) return this.error("You cannot confirm a trade that has empty offers. At least one player must offer something, in order to be able to complete the trade.");
                else if(t.isConfirmed(this.player.getId())) return this.error("You have already confirmed the trade. To remove your confirmation, edit the trade offer.");
                else if(!t.getOffer(this.player.getId()).isValid(this.playerData)) //TODO: maybe check which components are invalid and return that as a response
                {
                    t.getOffer(this.player.getId()).clear();
                    t.setConfirmed(this.player.getId(), false);
                    t.updateEmbed();
                    return this.error("Your trade offer is no longer valid (the contents did not match your inventory), and has been cleared. Please edit your offer again.");
                }

                t.setConfirmed(this.player.getId(), true);
                t.updateEmbed();

                this.ephemeral = true;
                this.response = "Successfully confirmed trade. Once both players confirm, the trade will be completed.";
            }
        }
        //Trade Edit Commands
        else
        {
            if(!Trade.isInTrade(this.player.getId()) || Trade.getTrade(this.player.getId()).isWaiting()) return this.error("You are not in a trade.");

            String subcommandGroup = Objects.requireNonNull(event.getSubcommandGroup());
            OptionMapping amountOption = event.getOption("amount");
            OptionMapping numberOption = event.getOption("number");
            OptionMapping nameOption = event.getOption("name");

            boolean add = subcommand.equals("add");
            boolean remove = subcommand.equals("remove") && (amountOption != null || numberOption != null);
            boolean clear = subcommand.equals("remove") && (amountOption == null && numberOption == null && nameOption == null);

            assert add || remove || clear;

            Trade trade = Trade.getTrade(this.player.getId());
            TradeOffer offer = trade.getOffer(this.player.getId());

            //Clear
            if(clear)
            {
                switch(subcommandGroup)
                {
                    case "credits" -> {
                        if(!offer.hasCredits()) return this.error("You have not offered any credits.");
                        offer.clearCredits();
                        this.response = "Removed credits from your Trade Offer.";
                    }
                    case "redeems" -> {
                        if(!offer.hasRedeems()) return this.error("You have not offered any redeems.");
                        offer.clearRedeems();
                        this.response = "Removed redeems from your Trade Offer.";
                    }
                    case "pokemon" -> {
                        if(!offer.hasPokemon()) return this.error("You have not offered any Pokemon.");
                        offer.clearPokemon();
                        this.response = "Removed all Pokemon from your Trade Offer.";
                    }
                    case "items" -> {
                        if(!offer.hasItems()) return this.error("You have not offered any items.");
                        offer.clearItems();
                        this.response = "Removed all items from your Trade Offer.";
                    }
                    case "tms" -> {
                        if(!offer.hasTMs()) return this.error("You have not offered any TMs.");
                        offer.clearTMs();
                        this.response = "Removed all TMs from your Trade Offer.";
                    }
                    default -> { return this.error(); }
                }
            }
            else
            {
                if(subcommandGroup.equals("pokemon"))
                {
                    int number = Objects.requireNonNull(numberOption).getAsInt();

                    if(number < 0 || number > this.playerData.getPokemonList().size()) return this.error("Invalid Pokemon number.");

                    Pokemon p = Objects.requireNonNull(Pokemon.build(this.playerData.getPokemonList().get(number - 1), number));
                    if(add)
                    {
                        if(offer.getPokemon().contains(p.getUUID())) return this.error(p.getName() + " is already included in your Trade Offer.");
                        offer.addPokemon(p.getUUID());
                    }
                    else
                    {
                        if(!offer.getPokemon().contains(p.getUUID())) return this.error(p.getName() + " is not included in your Trade Offer.");
                        offer.removePokemon(p.getUUID());
                    }
                }
                else
                {
                    int amount = amountOption == null ? 1 : amountOption.getAsInt();
                    String name = nameOption == null ? "" : nameOption.getAsString();

                    //Never allow negative amounts
                    if(amount <= 0) return this.error("Amount must be greater than 0.");

                    switch(subcommandGroup)
                    {
                        case "credits" -> {
                            if(add)
                            {
                                int newAmount = amount + offer.getCredits();
                                if(this.playerData.getCredits() < newAmount) return this.error("You do not have that many credits to offer.");
                                offer.addCredits(amount);
                            }
                            else offer.removeCredits(amount);
                        }
                        case "redeems" -> {
                            if(add)
                            {
                                int newAmount = amount + offer.getRedeems();
                                if(this.playerData.getRedeems() < newAmount) return this.error("You do not have that many redeems to offer.");
                                offer.addRedeems(amount);
                            }
                            else offer.removeRedeems(amount);
                        }
                        case "items" -> {
                            Item item = Item.cast(name);
                            if(item == null) return this.error("\"" + name + "\" is not a valid item name.");

                            if(add)
                            {
                                int newAmount = amount + offer.getItems().getOrDefault(item, 0);
                                if(this.playerData.getInventory().getItems().getOrDefault(item, 0) < newAmount) return this.error("You do not have that many %ss to offer.".formatted(item.getStyledName()));
                                offer.addItem(item, amount);
                            }
                            else offer.removeItem(item, amount);
                        }
                        case "tms" -> {
                            TM tm = TM.cast(name);
                            if(tm == null) return this.error("\"" + name + "\" is not a valid TM name.");

                            if(add)
                            {
                                int newAmount = amount + offer.getTMs().getOrDefault(tm, 0);
                                if(this.playerData.getInventory().getTMs().getOrDefault(tm, 0) < newAmount) return this.error("You do not have that many %ss to offer.".formatted(tm));
                                offer.addTM(tm, amount);
                            }
                            else offer.removeTM(tm, amount);
                        }
                        default -> { return this.error(); }
                    }
                }

                this.response = "Your offer has been successfully updated.";
                trade.removeBothConfirmed();
            }

            //If this point is reached, that means the above logic did not result in an error or early termination
            this.ephemeral = true;
            trade.updateEmbed();
        }

        return true;
    }
}
