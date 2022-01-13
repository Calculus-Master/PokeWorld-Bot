package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.Commands;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommandHelp extends Command
{
    public CommandHelp(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, true);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.VIEW_HELP)) return this.invalidMasteryLevel(Feature.VIEW_HELP);

        if(this.msg.length == 2 && Commands.isValid(this.msg[1]))
        {
            Commands.Registry command = Commands.getRegistry(this.msg[1]);
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

            List<Commands.Registry> selection = new ArrayList<>();
            Random r = new Random();
            for(int i = 0; i < 10; i++)
            {
                Commands.Registry reg = Commands.COMMANDS.get(r.nextInt(Commands.COMMANDS.size()));

                if(selection.contains(reg)) i--;
                else selection.add(reg);
            }

            for(Commands.Registry reg : selection) sb.append("`").append(this.serverData.getPrefix()).append("help ").append(reg.aliases.get(0)).append("`\n");

            this.embed.setDescription(sb.toString());
            this.embed.setTitle("Pokecord2 Help");
            this.embed.setFooter("There are too many commands to display here, so a random collection of Commands is displayed.");
        }
        return this;
    }
}
