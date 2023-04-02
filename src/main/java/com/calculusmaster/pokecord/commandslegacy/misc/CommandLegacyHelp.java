package com.calculusmaster.pokecord.commandslegacy.misc;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.commandslegacy.CommandsLegacy;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommandLegacyHelp extends CommandLegacy
{
    public CommandLegacyHelp(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, true);
    }

    @Override
    public CommandLegacy runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.VIEW_HELP)) return this.invalidMasteryLevel(Feature.VIEW_HELP);

        if(this.msg.length == 2 && CommandsLegacy.isValid(this.msg[1]))
        {
            CommandsLegacy.Registry command = CommandsLegacy.getRegistry(this.msg[1]);
            String prefix = this.serverData.getPrefix();

            StringBuilder terminalPoints = new StringBuilder();
            for(String s : command.help.keySet()) terminalPoints.append("`").append(prefix).append(s).append("` - ").append(command.help.get(s)).append("\n");

            if(command.help.isEmpty()) terminalPoints.append("Help Info has not been added for this command yet!");

            this.embed.setDescription(command.shortDesc);
            this.embed.setTitle("Pokecord2 Help");
            this.embed.addField("Possible Commands", terminalPoints.toString(), false);
        }
        else
        {
            StringBuilder sb = new StringBuilder("Enter the command name to see info about it.\nPossible Commands:\n\n");

            List<CommandsLegacy.Registry> selection = new ArrayList<>();
            Random r = new Random();
            for(int i = 0; i < 10; i++)
            {
                CommandsLegacy.Registry reg = CommandsLegacy.COMMANDS.get(r.nextInt(CommandsLegacy.COMMANDS.size()));

                if(selection.contains(reg)) i--;
                else selection.add(reg);
            }

            for(CommandsLegacy.Registry reg : selection) sb.append("`").append(this.serverData.getPrefix()).append("help ").append(reg.aliases.get(0)).append("`\n");

            this.embed.setDescription(sb.toString());
            this.embed.setTitle("Pokecord2 Help");
            this.embed.setFooter("There are too many commands to display here, so a random collection of Commands is displayed.");
        }
        return this;
    }
}
