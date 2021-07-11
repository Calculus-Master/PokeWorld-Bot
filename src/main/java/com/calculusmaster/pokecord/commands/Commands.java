package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.commands.config.CommandSettings;
import com.calculusmaster.pokecord.commands.duel.*;
import com.calculusmaster.pokecord.commands.economy.*;
import com.calculusmaster.pokecord.commands.misc.*;
import com.calculusmaster.pokecord.commands.moves.*;
import com.calculusmaster.pokecord.commands.pokemon.*;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.interfaces.ICommandCreator;
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
                .setCategory(Category.POKEMON);

        register("pokemon", "p")
                .setCommand(CommandPokemon::new)
                .setCategory(Category.POKEMON);

        register("moves", "m")
                .setCommand(CommandMoves::new)
                .setCategory(Category.MOVES);

        register("moveinfo", "mi")
                .setCommand(CommandMoveInfo::new)
                .setCategory(Category.MOVES);

        register("learn")
                .setCommand(CommandLearn::new)
                .setCategory(Category.MOVES);

        register("replace")
                .setCommand(CommandReplace::new)
                .setCategory(Category.MOVES);

        register("duel")
                .setCommand(CommandDuel::new)
                .setCategory(Category.DUEL);

        register("use")
                .setCommand(CommandUse::new)
                .setCategory(Category.DUEL);

        register("shop", "store")
                .setCommand(CommandShop::new)
                .setCategory(Category.ECONOMY);

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

    static class Registry
    {
        private List<String> aliases;
        private ICommandCreator builder;
        private Category category;
        private String shortDesc;
        private Map<String, String> help;

        Registry(String... aliases)
        {
            this.aliases = new ArrayList<>(Arrays.asList(aliases));
            this.help = new HashMap<>();
            this.category = Category.MISC;
            this.shortDesc = "";
        }

        Registry setCommand(ICommandCreator builder)
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
        POKEMON;
    }
}
