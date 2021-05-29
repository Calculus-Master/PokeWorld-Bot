package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;
import java.util.stream.Collectors;

public class CommandHelp extends Command
{
    public static final List<HelpEntry> MISC = new ArrayList<>();
    public static final List<HelpEntry> CONFIG = new ArrayList<>();
    public static final List<HelpEntry> POKEMON = new ArrayList<>();
    public static final List<HelpEntry> MOVES = new ArrayList<>();
    public static final List<HelpEntry> DUEL = new ArrayList<>();
    public static final List<HelpEntry> TRADE = new ArrayList<>();
    public static final List<HelpEntry> ECONOMY = new ArrayList<>();

    public CommandHelp(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 2)
        {
            HelpEntry help = this.allEntries().stream().filter(h -> h.command.contains(this.msg[1])).collect(Collectors.toList()).get(0);
            this.embed.setDescription(help.getHeadingFormat(this.serverData.getPrefix()) + "\n\n" + help.getBodyFormat());
        }
        else
        {
            StringBuilder sb = new StringBuilder("Enter the command name to see info about it.\nAvailable Commands:\n\n");
            for(HelpEntry h : this.allEntries()) sb.append("`").append(h.command.get(0)).append("`\n");
            this.embed.setDescription(sb.toString());
        }
        return this;
    }

    private List<HelpEntry> allEntries()
    {
        List<HelpEntry> entries = new ArrayList<>();
        entries.addAll(MISC);
        entries.addAll(CONFIG);
        entries.addAll(POKEMON);
        entries.addAll(MOVES);
        entries.addAll(DUEL);
        entries.addAll(TRADE);
        entries.addAll(ECONOMY);
        return entries;
    }

    public static class HelpEntry
    {
        private final List<String> command = new ArrayList<>();
        private String shortDesc;
        private final Map<Integer, List<String>> commandArgs;
        private final Map<String, String> argsDesc;

        private CommandCategory category;
        private int index;

        public HelpEntry(String command)
        {
            this.command.add(command);
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
                case MOVES -> CommandHelp.MOVES.add(this);
                case DUEL -> CommandHelp.DUEL.add(this);
                case TRADE -> CommandHelp.TRADE.add(this);
                case ECONOMY -> CommandHelp.ECONOMY.add(this);
            }
        }

        public HelpEntry addShortDescription(String desc)
        {
            this.shortDesc = desc;
            return this;
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
        MISC, CONFIG, POKEMON, MOVES, DUEL, TRADE, ECONOMY
    }
}
