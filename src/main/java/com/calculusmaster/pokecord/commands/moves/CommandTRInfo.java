package com.calculusmaster.pokecord.commands.moves;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.enums.items.TR;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.MoveData;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

public class CommandTRInfo extends Command
{
    public CommandTRInfo(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length < 2) this.sendMsg("Invalid Arguments!");
        else
        {
            String input = this.msg[1].replaceAll("tr", "");

            if(!input.chars().allMatch(Character::isDigit) || (Integer.parseInt(input) < 0 || Integer.parseInt(input) > 99))
            {
                this.sendMsg("Invalid TR Number!");
                return this;
            }

            TR tr = TR.get(Integer.parseInt(input));
            MoveData m = tr.getMoveData();

            String impl = Move.isImplemented(m.name) ? "" : "***Warning: Move is not implemented! You cannot use " + m.name + " in duels!***\n\n";
            String flavor = m.flavor.isEmpty() ? "*No Move Description*" : m.flavor.get(new Random().nextInt(m.flavor.size()));

            this.embed
                    .setTitle(tr + " Info (" + tr.getMoveName() + ")")
                    .setDescription(impl + flavor)
                    .addField("Type", Global.normalCase(m.type.toString()), true)
                    .addField("Category",Global.normalCase(m.category.toString()), true)
                    .addBlankField(true)
                    .addField("Power", String.valueOf(m.basePower), true)
                    .addField("Accuracy", String.valueOf(m.baseAccuracy), true);

            this.color = m.type.getColor();

            if(Move.CUSTOM_MOVES.contains(m.name)) this.embed.setFooter("This move has a custom implementation! It may not work exactly as described!");
        }

        return this;
    }
}