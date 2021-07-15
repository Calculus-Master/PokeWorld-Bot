package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.commands.config.CommandSettings;
import com.calculusmaster.pokecord.commands.duel.*;
import com.calculusmaster.pokecord.commands.economy.*;
import com.calculusmaster.pokecord.commands.misc.*;
import com.calculusmaster.pokecord.commands.moves.*;
import com.calculusmaster.pokecord.commands.pokemon.*;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.interfaces.CommandSupplier;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

public class Commands
{
    //TODO: Finish adding descriptions for all commands
    public static final List<Registry> COMMANDS = new ArrayList<>();

    public static void init()
    {
        register("start")
                .setCommand(CommandStart::new)
                .setCategory(Category.MISC)
                .setDesc("Start your Journey!")
                .addTerminalPoint("start", "Shows a list of all the possible starters.")
                .addTerminalPoint("start <starter>", "Start your journey with <starter>!");

        register("balance", "bal", "credits", "redeems", "c", "r")
                .setCommand(CommandBalance::new)
                .setCategory(Category.ECONOMY)
                .setDesc("View your balance!")
                .addTerminalPoint("balance", "Displays your credit, redeem and item balance!")
                .addTerminalPoint("balance <@player>", "Displays the balance of the player that is mentioned.");

        register("select")
                .setCommand(CommandSelect::new)
                .setCategory(Category.POKEMON)
                .setDesc("Select a Pokemon as your active one!")
                .addTerminalPoint("select <number>", "Selects the Pokemon at <number> to be your active Pokemon!");

        register("dex")
                .setCommand(CommandDex::new)
                .setCategory(Category.POKEMON)
                .setDesc("View generic information about a Pokemon by name, or your collections!")
                .addTerminalPoint("dex <name>", "Displays information about the Pokemon given by <name>. Optionally, type \"Shiny\" before <name> to show the shiny sprite, or \"Gigantamax\" before <name> to show the Gigantamax sprite (if applicable).")
                .addTerminalPoint("dex <number>", "Shows a page of your Pokedex collections. <number> can be left out, and this command will display page 1.");

        register("info")
                .setCommand(CommandInfo::new)
                .setCategory(Category.POKEMON)
                .setDesc("View specific information about one of your Pokemon!")
                .addTerminalPoint("info", "Displays information about your selected Pokemon.")
                .addTerminalPoint("info <number>", "Displays information about your Pokemon at <number>.")
                .addTerminalPoint("info latest", "Display information about your most recently acquired Pokemon.");

        register("catch")
                .setCommand(CommandCatch::new)
                .setCategory(Category.POKEMON)
                .setDesc("Catch a Pokemon!")
                .addTerminalPoint("catch <guess>", "Attempt to catch a spawn by guessing its name (<guess>).");

        register("pokemon", "p")
                .setCommand(CommandPokemon::new)
                .setCategory(Category.POKEMON)
                .setDesc("View your Pokemon!")
                .addTerminalPoint("pokemon", "View & sort your Pokemon list. There are many possible arguments.");

        register("moves", "m")
                .setCommand(CommandMoves::new)
                .setCategory(Category.MOVES)
                .setDesc("View your active Pokemon's moves!")
                .addTerminalPoint("moves", "Shows your active Pokemon's move set. In duels, this will show the move's type effectiveness against the opponent), otherwise, this will show your Pokemon's available moves.");

        register("moveinfo", "mi")
                .setCommand(CommandMoveInfo::new)
                .setCategory(Category.MOVES)
                .setDesc("View information about a specific move!")
                .addTerminalPoint("moveinfo <name>", "View more information about a move given by <name>. <name can also be a TM or TR (formatted like TM01 or TR00), and this will display information about the move that the TM/TR is linked to.");

        register("learn")
                .setCommand(CommandLearn::new)
                .setCategory(Category.MOVES)
                .setDesc("Learn a move!")
                .addTerminalPoint("learn <name>", "Learn a move from your Pokemon's available moves.");

        register("replace")
                .setCommand(CommandReplace::new)
                .setCategory(Category.MOVES)
                .setDesc("Replace a move in your move set!")
                .addTerminalPoint("replace <number>", "Must be used after `learn`. Replaces a move in your active Pokemon's current move set with one requested by the `learn` command.");

        register("duel")
                .setCommand(CommandDuel::new)
                .setCategory(Category.DUEL)
                .setDesc("Duel other players!")
                .addTerminalPoint("duel <@player>", "Send a duel request to the mentioned player.")
                .addTerminalPoint("duel confirm", "Accept a duel request.")
                .addTerminalPoint("duel deny", "Deny a duel request.")
                .addTerminalPoint("duel cancel", "Cancel a duel request you have sent to someone else.");

        register("use")
                .setCommand(CommandUse::new)
                .setCategory(Category.DUEL)
                .setDesc("Use a move or swap out Pokemon in a duel!")
                .addTerminalPoint("use <number>", "Use one of your learned moves. <number> must be between 1 and 4, inclusive.")
                .addTerminalPoint("use swap <number>", "Swap out your active Pokemon to another on your team (index given by <number>). Cannot be used in Wild Pokemon duels.")
                .addTerminalPoint("use z <number>", "Use a Z-Move of one of your learned moves given by <number>.")
                .addTerminalPoint("use d <number>", "Enter Dynamax, and then use one of your learned moves.");

        register("shop", "store")
                .setCommand(CommandShop::new)
                .setCategory(Category.ECONOMY)
                .setDesc("View the shop!")
                .addTerminalPoint("shop", "View a list of all shop categories. Use `shop <page>` to see the listings in a specific category.")
                .addTerminalPoint("shop <page>", "View a specific shop page, category given by <page>.");

        register("buy")
                .setCommand(CommandBuy::new)
                .setCategory(Category.ECONOMY);

        register("release")
                .setCommand(CommandRelease::new)
                .setCategory(Category.POKEMON);

        register("report", "bugreport", "suggest")
                .setCommand(CommandReport::new)
                .setCategory(Category.MISC);

        register("teach")
                .setCommand(CommandTeach::new)
                .setCategory(Category.POKEMON);

        register("inventory", "inv", "items", "tms", "trs")
                .setCommand(CommandInventory::new)
                .setCategory(Category.ECONOMY);

        register("help")
                .setCommand(CommandHelp::new)
                .setCategory(Category.MISC);

        register("trade")
                .setCommand(CommandTrade::new)
                .setCategory(Category.MISC);

        register("give")
                .setCommand(CommandGive::new)
                .setCategory(Category.POKEMON);

        register("market")
                .setCommand(CommandMarket::new)
                .setCategory(Category.ECONOMY);

        register("evolve")
                .setCommand(CommandEvolve::new)
                .setCategory(Category.POKEMON);

        register("equip")
                .setCommand(CommandEquip::new)
                .setCategory(Category.POKEMON);

        register("team")
                .setCommand(CommandTeam::new)
                .setCategory(Category.POKEMON);

        register("mega")
                .setCommand(CommandMega::new)
                .setCategory(Category.POKEMON);

        register("wildduel", "wild")
                .setCommand(CommandWildDuel::new)
                .setCategory(Category.DUEL);

        register("redeem")
                .setCommand(CommandRedeem::new)
                .setCategory(Category.POKEMON);

        register("trainerduel", "trainer", "fight")
                .setCommand(CommandTrainerDuel::new)
                .setCategory(Category.DUEL);

        register("gymduel", "gym", "challenge")
                .setCommand(CommandGymDuel::new)
                .setCategory(Category.DUEL);

        register("abilityinfo", "ai")
                .setCommand(CommandAbilityInfo::new)
                .setCategory(Category.MOVES);

        register("activate")
                .setCommand(CommandActivate::new)
                .setCategory(Category.POKEMON);

        register("pokepass", "pp", "bp")
                .setCommand(CommandPokePass::new)
                .setCategory(Category.MISC);

        register("favorites", "fav")
                .setCommand(CommandFavorites::new)
                .setCategory(Category.POKEMON);

        register("form")
                .setCommand(CommandForm::new)
                .setCategory(Category.POKEMON);

        register("flee", "concede", "surrender")
                .setCommand(CommandFlee::new)
                .setCategory(Category.DUEL);

        register("nickname", "nick")
                .setCommand(CommandNickname::new)
                .setCategory(Category.POKEMON);

        register("settings", "config")
                .setCommand(CommandSettings::new)
                .setCategory(Category.CONFIG);

        register("leaderboard", "lb", "lead")
                .setCommand(CommandLeaderboard::new)
                .setCategory(Category.MISC);

        register("bounties", "bounty", "tasks", "quests")
                .setCommand(CommandBounties::new)
                        .setCategory(Category.MISC);

        register("pursuit")
                .setCommand(CommandPursuit::new)
                .setCategory(Category.MISC);

        register("target")
                .setCommand(CommandTarget::new)
                .setCategory(Category.DUEL);

        register("tournament")
                .setCommand(CommandTournament::new)
                .setCategory(Category.DUEL);

        register("achievements")
                .setCommand(CommandAchievements::new)
                .setCategory(Category.MISC);

        register("dev")
                .setCommand(CommandDev::new)
                .setCategory(Category.MISC);
    }

    public static void execute(String input, MessageReceivedEvent event, String[] msg)
    {
        if(!PlayerDataQuery.isRegistered(event.getAuthor().getId()))
        {
            input = "start";
            msg = new String[]{"start"};
        }

        Command cmd = Commands.getCommand(input, event, msg).runCommand();

        if(!cmd.isNull()) event.getChannel().sendMessageEmbeds(cmd.getResponseEmbed()).queue();
    }

    public static boolean isValid(String input)
    {
        return COMMANDS.stream().anyMatch(r -> r.aliases.contains(input));
    }

    public static Command getCommand(String input, MessageReceivedEvent event, String[] msg)
    {
        Command c = new CommandInvalid(event, msg);

        Registry r = Commands.getRegistry(input);
        if(r != null) c = r.builder.create(event, msg);

        return c;
    }

    public static Registry getRegistry(String command)
    {
        for(Registry r : COMMANDS) if(r.aliases.contains(command)) return r;
        return null;
    }

    private static Registry register(String... aliases)
    {
        Registry r = new Registry(aliases);

        COMMANDS.add(r);

        return r;
    }

    public static class Registry
    {
        public List<String> aliases;
        private CommandSupplier builder;
        public Category category;
        public String shortDesc;
        public Map<String, String> help;

        Registry(String... aliases)
        {
            this.aliases = new ArrayList<>(Arrays.asList(aliases));
            this.help = new HashMap<>();
            this.category = Category.MISC;
            this.shortDesc = "";
        }

        Registry setCommand(CommandSupplier builder)
        {
            this.builder = builder;
            return this;
        }

        Registry setCategory(Category category)
        {
            this.category = category;
            return this;
        }

        Registry setDesc(String desc)
        {
            this.shortDesc = desc;
            return this;
        }

        Registry addTerminalPoint(String command, String description)
        {
            this.help.put(command, description);
            return this;
        }
    }

    enum Category
    {
        CONFIG,
        DUEL,
        ECONOMY,
        MISC,
        MOVES,
        POKEMON
    }
}
