package com.calculusmaster.pokecord.commandslegacy;

import com.calculusmaster.pokecord.commandslegacy.duel.*;
import com.calculusmaster.pokecord.commandslegacy.economy.CommandLegacyMarket;
import com.calculusmaster.pokecord.commandslegacy.misc.CommandLegacyDev;
import com.calculusmaster.pokecord.commandslegacy.misc.CommandLegacyHelp;
import com.calculusmaster.pokecord.commandslegacy.misc.CommandLegacySettings;
import com.calculusmaster.pokecord.commandslegacy.pokemon.CommandLegacyBreed;
import com.calculusmaster.pokecord.commandslegacy.pokemon.CommandLegacyPokemon;
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
        register("pokemon", "p")
                .setCommand(CommandLegacyPokemon::new)
                .setCategory(Category.POKEMON)
                .setDesc("View your Pokemon!")
                .addTerminalPoint("pokemon", "View & sort your Pokemon list. There are many possible arguments.");

        register("duel")
                .setCommand(CommandLegacyDuel::new)
                .setCategory(Category.DUEL)
                .setDesc("Duel other players!")
                .addTerminalPoint("duel <@player>", "Send a duel request to the mentioned player.")
                .addTerminalPoint("duel confirm", "Accept a duel request.")
                .addTerminalPoint("duel deny", "Deny a duel request.")
                .addTerminalPoint("duel cancel", "Cancel a duel request you have sent to someone else.");

        register("help")
                .setCommand(CommandLegacyHelp::new)
                .setCategory(Category.MISC)
                .setDesc("View help for commands!");

        register("market")
                .setCommand(CommandLegacyMarket::new)
                .setCategory(Category.ECONOMY);

        register("wildduel", "wild")
                .setCommand(CommandLegacyWildDuel::new)
                .setCategory(Category.DUEL);

        register("trainerduel", "trainer", "fight")
                .setCommand(CommandLegacyTrainerDuel::new)
                .setCategory(Category.DUEL);

        register("settings", "config")
                .setCommand(CommandLegacySettings::new)
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

        register("ztrialduel", "ztrial", "trial", "ztduel")
                .setCommand(CommandLegacyZTrialDuel::new)
                .setCategory(Category.DUEL);

        register("eliteduel", "elite")
                .setCommand(CommandLegacyEliteDuel::new)
                .setCategory(Category.DUEL);

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
