package com.calculusmaster.pokecord.commandslegacy;

import com.calculusmaster.pokecord.commandslegacy.duel.*;
import com.calculusmaster.pokecord.commandslegacy.economy.CommandLegacyBuy;
import com.calculusmaster.pokecord.commandslegacy.economy.CommandLegacyInventory;
import com.calculusmaster.pokecord.commandslegacy.economy.CommandLegacyMarket;
import com.calculusmaster.pokecord.commandslegacy.economy.CommandLegacyShop;
import com.calculusmaster.pokecord.commandslegacy.misc.*;
import com.calculusmaster.pokecord.commandslegacy.moves.CommandLegacyMoveDex;
import com.calculusmaster.pokecord.commandslegacy.moves.CommandLegacyMoves;
import com.calculusmaster.pokecord.commandslegacy.moves.CommandLegacyTMInfo;
import com.calculusmaster.pokecord.commandslegacy.moves.CommandLegacyTeach;
import com.calculusmaster.pokecord.commandslegacy.player.*;
import com.calculusmaster.pokecord.commandslegacy.pokemon.*;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.interfaces.CommandSupplier;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

public class CommandsLegacy
{
    public static final List<Registry> COMMANDS = new ArrayList<>();

    public static boolean COMMAND_THREAD_POOL;

    public static void init()
    {
        register("info")
                .setCommand(CommandLegacyInfo::new)
                .setCategory(Category.POKEMON)
                .setDesc("View specific information about one of your Pokemon!")
                .addTerminalPoint("info", "Displays information about your selected Pokemon.")
                .addTerminalPoint("info <number>", "Displays information about your Pokemon at <number>.")
                .addTerminalPoint("info latest", "Display information about your most recently acquired Pokemon.");

        register("pokemon", "p")
                .setCommand(CommandLegacyPokemon::new)
                .setCategory(Category.POKEMON)
                .setDesc("View your Pokemon!")
                .addTerminalPoint("pokemon", "View & sort your Pokemon list. There are many possible arguments.");

        register("moves", "m")
                .setCommand(CommandLegacyMoves::new)
                .setCategory(Category.MOVES)
                .setDesc("View your active Pokemon's moves!")
                .addTerminalPoint("moves", "Shows your active Pokemon's move set. In duels, this will show the move's type effectiveness against the opponent), otherwise, this will show your Pokemon's available moves.")
                .addTerminalPoint("moves info", "Shows information about all the available moves for your active Pokemon. This can help you create the perfect move set!");

        register("duel")
                .setCommand(CommandLegacyDuel::new)
                .setCategory(Category.DUEL)
                .setDesc("Duel other players!")
                .addTerminalPoint("duel <@player>", "Send a duel request to the mentioned player.")
                .addTerminalPoint("duel confirm", "Accept a duel request.")
                .addTerminalPoint("duel deny", "Deny a duel request.")
                .addTerminalPoint("duel cancel", "Cancel a duel request you have sent to someone else.");

        register("use")
                .setCommand(CommandLegacyUse::new)
                .setCategory(Category.DUEL)
                .setDesc("Use a move or swap out Pokemon in a duel!")
                .addTerminalPoint("use <number>", "Use one of your learned moves. <number> must be between 1 and 4, inclusive.")
                .addTerminalPoint("use swap <number>", "Swap out your active Pokemon to another on your team (index given by <number>). Cannot be used in Wild Pokemon duels.")
                .addTerminalPoint("use z <number>", "Use a Z-Move of one of your learned moves given by <number>.")
                .addTerminalPoint("use d <number>", "Enter Dynamax, and then use one of your learned moves.");

        register("shop", "store")
                .setCommand(CommandLegacyShop::new)
                .setCategory(Category.ECONOMY)
                .setDesc("View the shop!")
                .addTerminalPoint("shop", "View a list of all shop categories. Use `shop <page>` to see the listings in a specific category.")
                .addTerminalPoint("shop <page>", "View a specific shop page, category given by <page>.");

        register("buy")
                .setCommand(CommandLegacyBuy::new)
                .setCategory(Category.ECONOMY)
                .setDesc("Buy items from the shop!")
                .addTerminalPoint("buy nature <nature>", "Buy a nature for your selected Pokemon, where <nature> is the name of the nature.")
                .addTerminalPoint("buy candy <number>", "Buy a specific number of rare candies. <number> is optional, and will default to 1.")
                .addTerminalPoint("buy item <index> <amount>", "Buy an item listed on the shop. <amount> is optional and will default to 1.")
                .addTerminalPoint("buy form <name>", "Buy a form for your selected Pokemon, if applicable. Bought Forms are permanent, so you only need to purchase the form once to be able to transform any of your Pokemon of the same kind. The shop page for forms dynamically changes based on your selected Pokemon, and shows you the possible forms you can buy.")
                .addTerminalPoint("buy mega <x:y>", "Buy the Mega Evolution for your selected Pokemon, if available. Bought Mega Evolutions are permanent, so you only need to purchase the form once to be able to Mega Evolve any of your Pokemon of the same kind. For Pokemon with a single Mega Evolution, omit the <x:y> argument. For Pokemon with both X and Y Mega Evolutions, you have to specify which one to buy.")
                .addTerminalPoint("buy tm <number>", "Buy an available TM from the shop. <number> can either be a number, such as 69, or formatted as a TM, like TM69.")
                .addTerminalPoint("buy tr <number>", "Buy an available TR from the shop. <number> can either be a number, such as 69, or formatted as a TR, like TR69.")
                .addTerminalPoint("buy movetutor <move>", "Buy a Move Tutor move. If valid, the move will be automatically inserted into the first slot of your selected Pokemon's move set.")
                .addTerminalPoint("buy zcrystal <name>", "Buy an available Z Crystal from the shop. You cannot buy or own multiple of the same Z Crystals.");

        register("release")
                .setCommand(CommandLegacyRelease::new)
                .setCategory(Category.POKEMON)
                .setDesc("Release a Pokemon into the wild!")
                .addTerminalPoint("release <number>", "Start a release request for your Pokemon at <number>. You will then be prompted to confirm or deny.")
                .addTerminalPoint("release confirm", "Confirm a release request. Your Pokemon will be deleted forever!")
                .addTerminalPoint("release deny", "Deny a release request. Your Pokemon will remain unchanged.");

        register("report", "bugreport", "suggest")
                .setCommand(CommandLegacyReport::new)
                .setCategory(Category.MISC)
                .setDesc("Submit a bug report or suggestion!")
                .addTerminalPoint("report <content>", "Replace <content> with what you want to report. The report will include anything you type after the initial command.");

        register("teach")
                .setCommand(CommandLegacyTeach::new)
                .setCategory(Category.POKEMON)
                .setDesc("Teach TMs and TRs to your Pokemon!")
                .addTerminalPoint("teach tm <number>", "Teach a TM to your selected Pokemon.")
                .addTerminalPoint("teach tr <number>", "Teach a TR to your selected Pokemon.");

        register("inventory", "inv", "items", "tms", "trs")
                .setCommand(CommandLegacyInventory::new)
                .setCategory(Category.ECONOMY)
                .setDesc("View your inventory: items, TMs, TRs, and Z-Crystals!")
                .addTerminalPoint("inventory <page>", "View a specific subpage of your inventory in more detail.");

        register("help")
                .setCommand(CommandLegacyHelp::new)
                .setCategory(Category.MISC)
                .setDesc("View help for commands!");

        register("trade")
                .setCommand(CommandLegacyTrade::new)
                .setCategory(Category.PLAYER);

        register("give")
                .setCommand(CommandLegacyGive::new)
                .setCategory(Category.POKEMON);

        register("market")
                .setCommand(CommandLegacyMarket::new)
                .setCategory(Category.ECONOMY);

        register("equip")
                .setCommand(CommandLegacyEquip::new)
                .setCategory(Category.POKEMON);

        register("team")
                .setCommand(CommandLegacyTeam::new)
                .setCategory(Category.POKEMON);

        register("wildduel", "wild")
                .setCommand(CommandLegacyWildDuel::new)
                .setCategory(Category.DUEL);

        register("redeem")
                .setCommand(CommandLegacyRedeem::new)
                .setCategory(Category.POKEMON);

        register("trainerduel", "trainer", "fight")
                .setCommand(CommandLegacyTrainerDuel::new)
                .setCategory(Category.DUEL);

        register("activate")
                .setCommand(CommandLegacyActivate::new)
                .setCategory(Category.POKEMON);

        register("favorites", "fav")
                .setCommand(CommandLegacyFavorites::new)
                .setCategory(Category.POKEMON);

        register("form")
                .setCommand(CommandLegacyForm::new)
                .setCategory(Category.POKEMON);

        register("flee", "concede", "surrender")
                .setCommand(CommandLegacyFlee::new)
                .setCategory(Category.DUEL);

        register("nickname", "nick")
                .setCommand(CommandLegacyNickname::new)
                .setCategory(Category.POKEMON);

        register("settings", "config")
                .setCommand(CommandLegacySettings::new)
                .setCategory(Category.MISC);

        register("leaderboard", "lb", "lead")
                .setCommand(CommandLegacyLeaderboard::new)
                .setCategory(Category.PLAYER);

        register("bounties", "bounty", "tasks", "quests")
                .setCommand(CommandLegacyBounties::new)
                .setCategory(Category.PLAYER);

        register("target")
                .setCommand(CommandLegacyTarget::new)
                .setCategory(Category.DUEL);

        register("tournament")
                .setCommand(CommandLegacyTournament::new)
                .setCategory(Category.DUEL);

        register("achievements")
                .setCommand(CommandLegacyAchievements::new)
                .setCategory(Category.PLAYER);

        register("serverinfo", "server")
                .setCommand(CommandLegacyServerInfo::new)
                .setCategory(Category.MISC);

        register("profile")
                .setCommand(CommandLegacyProfile::new)
                .setCategory(Category.PLAYER);

        register("tip", "tips")
                .setCommand(CommandLegacyTip::new)
                .setCategory(Category.MISC);

        register("location", "time", "region")
                .setCommand(CommandLegacyLocation::new)
                .setCategory(Category.MISC);

        register("gauntlet", "gauntletduel")
                .setCommand(CommandLegacyGauntletDuel::new)
                .setCategory(Category.DUEL);

        register("raidduel", "raid")
                .setCommand(CommandLegacyRaidDuel::new)
                .setCategory(Category.DUEL);

        register("breed")
                .setCommand(CommandLegacyBreed::new)
                .setCategory(Category.POKEMON);

        register("eggs", "egg")
                .setCommand(CommandLegacyEggs::new)
                .setCategory(Category.POKEMON);

        register("tminfo", "tmi")
                .setCommand(CommandLegacyTMInfo::new)
                .setCategory(Category.MOVES);

        register("level")
                .setCommand(CommandLegacyLevel::new)
                .setCategory(Category.PLAYER);

        register("ztrialduel", "ztrial", "trial", "ztduel")
                .setCommand(CommandLegacyZTrialDuel::new)
                .setCategory(Category.DUEL);

        register("eliteduel", "elite")
                .setCommand(CommandLegacyEliteDuel::new)
                .setCategory(Category.DUEL);

        register("learninfo", "li", "movedex", "md")
                .setCommand(CommandLegacyMoveDex::new)
                .setCategory(Category.MOVES);

        register("prestige")
                .setCommand(CommandLegacyPrestige::new)
                .setCategory(Category.POKEMON);

        register("augments")
                .setCommand(CommandLegacyAugments::new)
                .setCategory(Category.POKEMON);

        register("augmentinfo", "auginfo")
                .setCommand(CommandLegacyAugmentInfo::new)
                .setCategory(Category.POKEMON);

        register("trainerinfo", "ti")
                .setCommand(CommandLegacyTrainerInfo::new)
                .setCategory(Category.DUEL);

        register("dev")
                .setCommand(CommandLegacyDev::new)
                .setCategory(Category.MISC);
    }

    public static void execute(String input, MessageReceivedEvent event, String[] msg)
    {
        if(!PlayerDataQuery.isRegistered(event.getAuthor().getId()) && !(input.equals("start") && msg.length == 2))
        {
            input = "start";
            msg = new String[]{"start"};
        }

        CommandsLegacy.getCommand(input, event, msg).runCommand().send();
    }

    public static boolean isValid(String input)
    {
        return COMMANDS.stream().anyMatch(r -> r.aliases.contains(input));
    }

    public static CommandLegacy getCommand(String input, MessageReceivedEvent event, String[] msg)
    {
        CommandLegacy c = new CommandLegacyInvalid(event, msg);

        Registry r = CommandsLegacy.getRegistry(input);
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
        DUEL,
        ECONOMY,
        MISC,
        MOVES,
        PLAYER,
        POKEMON;
    }
}
