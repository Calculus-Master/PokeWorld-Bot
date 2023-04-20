package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Ability;
import com.calculusmaster.pokecord.game.enums.elements.EggGroup;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.player.level.MasteryLevelManager;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.sort.PokemonListOrderType;
import com.calculusmaster.pokecord.game.pokemon.sort.PokemonListSorter;
import com.calculusmaster.pokecord.game.world.MarketEntry;
import com.calculusmaster.pokecord.game.world.PokeWorldMarket;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.StatisticType;
import kotlin.Pair;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.*;

import static com.calculusmaster.pokecord.game.pokemon.sort.PokemonListOrderType.*;

public class CommandMarket extends PokeWorldCommand
{
    public static final int MIN_PRICE = 50;
    public static final int MARKET_POKEMON_PER_PAGE = 20;

    private static final Map<String, String> MARKET_BUY_REQUESTS = new HashMap<>();

    public static void init()
    {
        CommandData
                .create("market")
                .withConstructor(CommandMarket::new)
                .withFeature(Feature.ACCESS_MARKET)
                .withCommand(Commands
                        .slash("market", "Buy and Sell Pokemon on the %s Market!".formatted(Pokeworld.NAME))
                        .addSubcommands(
                                new SubcommandData("view", "View Market listings.")
                                        .addOption(OptionType.INTEGER, "page", "The page of the Market to view.", false)
                                        .addOption(OptionType.STRING, "custom", "Add a custom filter query for the Market list.", false)
                                        //Popular queries - made into option types
                                        .addOption(OptionType.STRING, "order", "The order to sort Pokemon in. (Default: by time)", false)
                                        .addOption(OptionType.BOOLEAN, "descending", "Whether to sort Pokemon in descending order. (Default: descending)", false)
                                        .addOption(OptionType.BOOLEAN, "shiny", "Filter Pokemon by whether they are shiny or not.", false)
                                        .addOption(OptionType.BOOLEAN, "mastered", "Filter Pokemon by whether they are mastered or not.", false)
                                        .addOption(OptionType.STRING, "name", "Filter Pokemon by name.", false)
                                        .addOption(OptionType.STRING, "stat", "Filter Pokemon by stat values.", false)
                                        .addOption(OptionType.STRING, "iv", "Filter Pokemon by IV values.", false)
                                        .addOption(OptionType.STRING, "ev", "Filter Pokemon by EV values.", false)
                                        .addOption(OptionType.STRING, "type", "Filter Pokemon by Type.", false, true)
                                        .addOption(OptionType.STRING, "rarity", "Filter Pokemon by Rarity.", false, true)
                                        .addOption(OptionType.STRING, "egg-group", "Filter Pokemon by Egg Group.", false, true)
                                        .addOption(OptionType.STRING, "ability", "Filter Pokemon by Ability.", false, true)
                                        .addOption(OptionType.STRING, "level", "Filter Pokemon by level.", false)
                                        .addOption(OptionType.STRING, "dynamax-level", "Filter Pokemon by Dynamax Level.", false)
                                        .addOption(OptionType.STRING, "prestige-level", "Filter Pokemon by Prestige Level.", false)
                                        .addOption(OptionType.STRING, "price", "Filter Pokemon by Price.", false)
                                ,
                                new SubcommandData("buy", "Buy an item from the Market.")
                                        .addOption(OptionType.STRING, "market-id", "The Market ID of the Pokemon you want to buy.", true),
                                new SubcommandData("sell", "List your Pokemon for sale on the Market.")
                                        .addOption(OptionType.INTEGER, "pokemon-number", "The number of the Pokemon you want to list.", true)
                                        .addOption(OptionType.INTEGER, "price", "The price you want to list the Pokemon for.", true),
                                new SubcommandData("confirm", "Confirm a buy request."),
                                new SubcommandData("cancel", "Cancel a buy request."),
                                new SubcommandData("withdraw", "Remove a Pokemon you've listed for sale from the Market.")
                                        .addOption(OptionType.STRING, "market-id", "The Market ID of the Pokemon you want to withdraw.", true),
                                new SubcommandData("info", "View information about a Pokemon listed on the Market.")
                                        .addOption(OptionType.STRING, "market-id", "The Market ID of the Pokemon you want to view.", true),
                                new SubcommandData("listings", "View the Pokemon you've listed (put up for sale) on the Market.")
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        if(subcommand.equals("view"))
        {
            OptionMapping customOption = event.getOption("custom");

            List<Pokemon> pokemonList = PokeWorldMarket.getPokemon();
            List<String> query = customOption == null ? new ArrayList<>() : new ArrayList<>(List.of(customOption.getAsString().split("\\s(?=(([^\"]*\"){2})*[^\"]*$)\\s*")));

            //Popular query (that are option types) parsed here
            Pair<List<String>, List<String>> parsedOptions = PokemonListSorter.parsePriorityQueries(event);
            query.addAll(parsedOptions.getFirst());
            List<String> errors = new ArrayList<>(parsedOptions.getSecond());

            //Create sorter
            PokemonListSorter sorter = new PokemonListSorter(true, pokemonList.stream().distinct().toList(), query);

            //Filter
            errors.addAll(sorter.filter());

            if(!errors.isEmpty())
                this.playerData.directMessage("*Your recent `/market view` command had some errors. The following inputs were invalid*:\n```\n" + String.join("\n", errors) + "\n```");

            //Sort
            List<Pokemon> pokemon = sorter.sort(); //TODO: Settings for sorting
            if(pokemon.isEmpty()) return this.error("None of the Market Pokemon matched your given filters.");

            OptionMapping pageOption = event.getOption("page");
            int maxPage = 1 + pokemon.size() / MARKET_POKEMON_PER_PAGE;
            int page = pageOption == null || pageOption.getAsInt() < 0 ? 1 : Math.min(pageOption.getAsInt(), maxPage);

            int startIndex = (page - 1) * MARKET_POKEMON_PER_PAGE;
            int endIndex = Math.min(startIndex + MARKET_POKEMON_PER_PAGE, pokemon.size());

            List<String> pokemonHeaders = new ArrayList<>(), pokemonPrices = new ArrayList<>(), pokemonInfo = new ArrayList<>();
            PokemonListOrderType sortType = sorter.getSortType().getFirst();

            for(int i = startIndex; i < endIndex; i++)
            {
                Pokemon p = pokemon.get(i);
                MarketEntry e = PokeWorldMarket.getMarketEntry(p);

                List<String> tags = new ArrayList<>();
                if(p.isShiny()) tags.add(":star2:");
                if(p.getPrestigeLevel() != 0) tags.add(":zap:");

                pokemonHeaders.add("**%s** (Level `%s`) %s".formatted(
                        p.getName(),
                        p.getLevel(),
                        String.join("", tags)
                ));

                pokemonPrices.add("`%s`c | ID: %s".formatted(e.getPrice(), e.getMarketID()));

                if(sortType.equals(STAT)) pokemonInfo.add("`%s`".formatted(String.valueOf(p.getTotalStat())));
                else if(sortType.equals(IV)) pokemonInfo.add("`%s`".formatted(p.getTotalIV()));
                else if(sortType.equals(EV)) pokemonInfo.add("`%s`".formatted(String.valueOf(p.getTotalEV())));
                else pokemonInfo.add("<t:%s:T>".formatted(e.getTimestamp()));
            }

            String footer = """
                Sorted by: %s (%s)
                Total Listings: %s (Filter: %s)
                View a listing in more detail using /market info!
                """.formatted(
                    sortType.getName(), sorter.getSortType().getFirst().equals(RANDOM) ? "N/A" : sorter.getSortType().getSecond() ? "Descending" : "Ascending",
                    pokemonList.size(), pokemon.size());

            this.embed
                    .setTitle("The " + Pokeworld.NAME + " Market")
                    .setDescription("""
                        Page **%s** of **%s**
                        """.formatted(page, maxPage))

                    .addField("Pokemon", String.join("\n", pokemonHeaders), true)
                    .addField("Price | ID", String.join("\n", pokemonPrices), true)
                    .addField(sortType.equals(STAT) ? "Total Stat" : sortType.equals(IV) ? "Total IV" : sortType.equals(EV) ? "Total EV" : "Time Listed", String.join("\n", pokemonInfo), true)

                    .setFooter(footer);
        }
        else if(subcommand.equals("buy"))
        {
            OptionMapping marketIDOption = Objects.requireNonNull(event.getOption("market-id"));
            String marketID = marketIDOption.getAsString();

            MarketEntry entry = PokeWorldMarket.getMarketEntry(marketID);
            if(entry == null) return this.error("Invalid Market ID.");

            int cost = entry.getPrice();
            if(entry.getSellerID().equals(this.player.getId())) return this.error("You cannot buy a Pokemon you've listed.");
            else if(this.playerData.getCredits() < cost) return this.error("You do not have enough credits to buy this Pokemon. You need **" + cost + "**c.");

            MARKET_BUY_REQUESTS.put(this.player.getId(), marketID);

            this.response = "Are you sure you want to buy the **Level %s %s** from the Market for **%sc** (Market ID: %s)?\nUse `/market confirm` to confirm, or `/market cancel` to cancel.".formatted(
                    entry.getPokemon().getLevel(), entry.getPokemon().getName(),
                    entry.getPrice(),
                    entry.getMarketID()
            );
        }
        else if(subcommand.equals("sell"))
        {
            OptionMapping numberOption = Objects.requireNonNull(event.getOption("pokemon-number"));
            int num = numberOption.getAsInt();
            OptionMapping priceOption = Objects.requireNonNull(event.getOption("price"));
            int price = priceOption.getAsInt();

            if(num < 1 || num > this.playerData.getPokemonList().size()) return this.error("Invalid Pokemon number.");
            else if(price < MIN_PRICE) return this.error("Invalid price. The minimum price you can sell a Pokemon for is " + MIN_PRICE + "c.");

            List<MarketEntry> listings = PokeWorldMarket.getListings(this.player.getId());

            if(listings.size() >= MasteryLevelManager.getMaxMarketListings(this.playerData.getLevel()))
                return this.error("You have reached your maximum number of Market Listings. Either withdraw a listing or wait for one to be sold before you can list another Pokemon!");

            Pokemon pokemon = Pokemon.build(this.playerData.getPokemonList().get(num - 1));
            MarketEntry entry = new MarketEntry(this.player.getId(), pokemon.getUUID(), price, this.player.getName());

            PokeWorldMarket.insertMarketEntry(entry);
            this.playerData.removePokemon(pokemon.getUUID());

            this.response = "Listed your **Level %s %s** for sale on the Market for **%sc** (Market ID: %s)!".formatted(
                    pokemon.getLevel(), pokemon.getName(),
                    price,
                    entry.getMarketID()
            );
        }
        else if(subcommand.equals("confirm") || subcommand.equals("cancel"))
        {
            if(!MARKET_BUY_REQUESTS.containsKey(this.player.getId()))
                return this.error("You have not requested to buy any listing. Use `/market buy` to request to buy a Pokemon first.");

            MarketEntry entry = PokeWorldMarket.getMarketEntry(MARKET_BUY_REQUESTS.get(this.player.getId()));
            if(entry == null) return this.error();

            if(subcommand.equals("confirm"))
            {
                if(this.playerData.getCredits() < entry.getPrice())
                {
                    MARKET_BUY_REQUESTS.remove(this.player.getId());
                    return this.error("You do not have enough credits to buy this Pokemon. Required: **" + entry.getPrice() + "**c.");
                }

                this.playerData.changeCredits(-entry.getPrice());
                this.playerData.addPokemon(entry.getPokemonID());

                this.playerData.getStatistics().increase(StatisticType.POKEMON_BOUGHT_MARKET);
                PlayerDataQuery.ofNonNull(entry.getSellerID()).getStatistics().increase(StatisticType.POKEMON_SOLD_MARKET);

                PokeWorldMarket.deleteMarketEntry(entry.getMarketID());

                this.response = "You purchased a **Level %s %s** from the Market for **%sc** (Market ID: %s)!".formatted(
                        entry.getPokemon().getLevel(), entry.getPokemon().getName(),
                        entry.getPrice(),
                        entry.getMarketID()
                );
            }
            else
            {
                MARKET_BUY_REQUESTS.remove(this.player.getId());
                this.response = "Request cancelled."; this.ephemeral = true;
            }
        }
        else if(subcommand.equals("withdraw"))
        {
            OptionMapping marketIDOption = Objects.requireNonNull(event.getOption("market-id"));
            String marketID = marketIDOption.getAsString();

            MarketEntry entry = PokeWorldMarket.getMarketEntry(marketID);
            if(entry == null) return this.error("Invalid Market ID.");
            else if(!entry.getSellerID().equals(this.player.getId())) return this.error("You cannot withdraw a listing that you did not make.");

            PokeWorldMarket.deleteMarketEntry(marketID);

            this.playerData.addPokemon(entry.getPokemonID());

            this.response = "You have withdrawn your **Level %s %s** from the Market.".formatted(
                    entry.getPokemon().getLevel(), entry.getPokemon().getName()
            );
        }
        else if(subcommand.equals("info"))
        {

        }
        else if(subcommand.equals("listings"))
        {
            List<MarketEntry> listings = PokeWorldMarket.getListings(this.player.getId());

            if(listings.isEmpty()) return this.error("You have not listed any Pokemon for sale on the Market.");

            List<String> pokemonHeaders = new ArrayList<>(), pokemonPrices = new ArrayList<>(), pokemonTime = new ArrayList<>();

            for(MarketEntry e : listings)
            {
                Pokemon p = e.getPokemon();

                List<String> tags = new ArrayList<>();
                if(p.isShiny()) tags.add(":star2:");
                if(p.getPrestigeLevel() != 0) tags.add(":zap:");

                pokemonHeaders.add("**Level %s %s**%s".formatted(
                        p.getLevel(),
                        p.getName(),
                        String.join("", tags)
                ));

                pokemonPrices.add("`%s`c | ID: %s".formatted(e.getPrice(), e.getMarketID()));
                pokemonTime.add("<t:%s:T>".formatted(e.getTimestamp()));
            }

            String footer = """
                Remove a listing with /market withdraw!
                """;

            this.embed
                    .setTitle("The " + Pokeworld.NAME + " Market: " + this.player.getName() + "'s Listings")
                    .setDescription("""
                        *These are all the Pokemon you've listed for sale on the Market.*
                        """)

                    .addField("Pokemon", String.join("\n", pokemonHeaders), true)
                    .addField("Price | ID", String.join("\n", pokemonPrices), true)
                    .addField("Time Listed", String.join("\n", pokemonTime), true)

                    .setFooter(footer);
        }

        return true;
    }

    @Override
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event)
    {
        String option = event.getFocusedOption().getName();

        List<String> sourceList = switch(option)
        {
            case "type" -> Arrays.stream(Type.values()).map(Type::getStyledName).toList();
            case "rarity" -> Arrays.stream(PokemonRarity.Rarity.values()).map(PokemonRarity.Rarity::getName).toList();
            case "egg-group" -> Arrays.stream(EggGroup.values()).map(EggGroup::getName).toList();
            case "move" -> Arrays.stream(MoveEntity.values()).map(MoveEntity::getName).toList();
            case "ability" -> Arrays.stream(Ability.values()).map(Ability::getName).toList();
            default -> new ArrayList<>();
        };

        String input = event.getFocusedOption().getValue();
        if(!sourceList.isEmpty()) event.replyChoiceStrings(this.getAutocompleteOptions(input, sourceList)).queue();

        return true;
    }
}
