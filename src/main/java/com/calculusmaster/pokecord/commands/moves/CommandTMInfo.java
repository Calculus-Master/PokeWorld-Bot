package com.calculusmaster.pokecord.commands.moves;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.data.MoveData;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

public class CommandTMInfo extends Command
{
    public CommandTMInfo(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.ACCESS_TMS)) return this.invalidMasteryLevel(Feature.ACCESS_TMS);

        if(this.msg.length < 2) this.response = "Invalid Arguments!";
        else
        {
            String input = this.msg[1].replaceAll("tm", "");

            if(!input.chars().allMatch(Character::isDigit) || (Integer.parseInt(input) < 1 || Integer.parseInt(input) > 100))
            {
                this.response = "Invalid TM Number!";
                return this;
            }

            TM tm = TM.cast(input);
            MoveData m = tm.getMove().data();

            String impl = Move.isImplemented(m.getEntity()) ? "" : "***Warning: Move is not implemented! You cannot use " + m.getName() + " in duels!***\n\n";
            String flavor = m.getFlavorText().isEmpty() ? "*No Move Description*" : m.getFlavorText().get(new Random().nextInt(m.getFlavorText().size()));

            this.embed
                    .setTitle(tm + " Info (" + m.getName() + ")")
                    .setDescription(impl + flavor)
                    .addField("Type", Global.normalize(m.getType().toString()), true)
                    .addField("Category", Global.normalize(m.getCategory().toString()), true)
                    .addBlankField(true)
                    .addField("Power", String.valueOf(m.getBasePower()), true)
                    .addField("Accuracy", String.valueOf(m.getBaseAccuracy()), true);

            this.color = m.getType().getColor();

            if(Move.CUSTOM_MOVES.contains(m.getEntity())) this.embed.setFooter("This move has a custom implementation! It may not work exactly as described!");
        }

        return this;
    }
}
