package com.calculusmaster.pokecord.commandslegacy;

import com.calculusmaster.pokecord.commandslegacy.duel.*;
import com.calculusmaster.pokecord.commandslegacy.economy.CommandLegacyMarket;
import com.calculusmaster.pokecord.commandslegacy.misc.CommandLegacyDev;
import com.calculusmaster.pokecord.commandslegacy.misc.CommandLegacyHelp;
import com.calculusmaster.pokecord.commandslegacy.misc.CommandLegacyServerInfo;
import com.calculusmaster.pokecord.commandslegacy.misc.CommandLegacySettings;
import com.calculusmaster.pokecord.commandslegacy.moves.CommandLegacyMoves;
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

        register("serverinfo", "server")
                .setCommand(CommandLegacyServerInfo::new)
                .setCategory(Category.MISC);

        register("profile")
                .setCommand(CommandLegacyProfile::new)
                .setCategory(Category.PLAYER);

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

        register("level")
                .setCommand(CommandLegacyLevel::new)
                .setCategory(Category.PLAYER);

        register("ztrialduel", "ztrial", "trial", "ztduel")
                .setCommand(CommandLegacyZTrialDuel::new)
                .setCategory(Category.DUEL);

        register("eliteduel", "elite")
                .setCommand(CommandLegacyEliteDuel::new)
                .setCategory(Category.DUEL);

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
