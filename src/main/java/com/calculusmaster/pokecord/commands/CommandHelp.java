package com.calculusmaster.pokecord.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;
import java.util.stream.Collectors;

public class CommandHelp extends Command
{
    public static final List<HelpEntry> MISC = new ArrayList<>();
    public static final List<HelpEntry> CONFIG = new ArrayList<>();
    public static final List<HelpEntry> POKEMON = new ArrayList<>();
    public static final List<HelpEntry> DUEL = new ArrayList<>();
    public static final List<HelpEntry> TRADE = new ArrayList<>();
    public static final List<HelpEntry> ECONOMY = new ArrayList<>();

    public CommandHelp(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "help <page>");
    }

    @Override
    public Command runCommand()
    {
        this.embed.setDescription(MISC.get(0).getHeadingFormat(this.serverData.getPrefix()) + "\n\n" + MISC.get(0).getBodyFormat());
        return this;
    }

    public static class HelpEntry
    {
        private final List<String> command = new ArrayList<>();
        private final String shortDesc;
        private final Map<Integer, List<String>> commandArgs;
        private final Map<String, String> argsDesc;

        private CommandCategory category;
        private int index;

        HelpEntry(String command, String shortDesc)
        {
            this.command.add(command);
            this.shortDesc = shortDesc;
            this.commandArgs = new HashMap<>();
            this.argsDesc = new HashMap<>();
            this.index = 1;
        }

        private void addToList()
        {
            switch(this.category)
            {
                case MISC -> CommandHelp.MISC.add(this);
                case CONFIG -> CommandHelp.CONFIG.add(this);
                case POKEMON -> CommandHelp.POKEMON.add(this);
                case DUEL -> CommandHelp.DUEL.add(this);
                case TRADE -> CommandHelp.TRADE.add(this);
                case ECONOMY -> CommandHelp.ECONOMY.add(this);
            }
        }

        public HelpEntry setCategory(CommandCategory c)
        {
            this.category = c;
            this.addToList();
            return this;
        }

        public HelpEntry addArgs(String... argOptions)
        {
            List<String> args = new ArrayList<>();
            Collections.addAll(args, argOptions);
            this.commandArgs.put(this.index, args);
            this.index++;
            return this;
        }

        public HelpEntry addArgDesc(String arg, String desc)
        {
            this.argsDesc.put(arg, desc);
            return this;
        }

        public HelpEntry addAliases(String... aliases)
        {
            Collections.addAll(this.command, aliases);
            return this;
        }

        public boolean contains(String s)
        {
            return this.command.contains(s);
        }

        String getHeadingFormat(String serverPrefix)
        {
            StringBuilder header = new StringBuilder().append("`" + serverPrefix + this.command.get(0) + " ");
            for(Integer i : this.commandArgs.keySet()) header.append(this.getArgCluster(i));
            return header.append("` – ").append(this.shortDesc).toString();
        }

        String getBodyFormat()
        {
            StringBuilder body = new StringBuilder();

            if(this.command.size() != 1)
            {
                body.append("Aliases: `");
                for(String s : this.command) body.append(s + ", ");
                body.deleteCharAt(body.lastIndexOf(" ")).deleteCharAt(body.lastIndexOf(",")).append("`");
            }
            body.append("\n");

            for(String s : this.argsDesc.keySet()) body.append("`" + s + "`").append(" – " + this.argsDesc.get(s)).append("\n");
            return body.toString();
        }

        private String getArgCluster(int i)
        {
            String mid = this.commandArgs.get(i).stream().map(s -> s + ":").collect(Collectors.joining());
            return "<" + mid.substring(0, mid.lastIndexOf(":")) + mid.substring(mid.lastIndexOf(":") + 1) + ">";
        }
    }

    public enum CommandCategory
    {
        MISC, CONFIG, POKEMON, DUEL, TRADE, ECONOMY
    }
}
