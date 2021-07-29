package com.calculusmaster.pokecord.commands.moves;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.MoveData;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

public class CommandMoveInfo extends Command
{
    public CommandMoveInfo(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length < 2)
        {
            this.embed.setDescription(CommandInvalid.getShort());
        }
        else
        {
            String moveString = Global.normalCase(this.getMultiWordContent(1));
            boolean isTM = moveString.startsWith("Tm");
            boolean isTR = moveString.startsWith("Tr");

            if(!Move.isMove(moveString) && !(isTM || isTR))
            {
                this.embed.setDescription(CommandInvalid.getShort());
                return this;
            }

            MoveData m;

            if(isTM) m = TM.get(Integer.parseInt(moveString.substring(2))).getMoveData();
            else if(isTR) m = TR.get(Integer.parseInt(moveString.substring(2))).getMoveData();
            else m = DataHelper.moveData(moveString);

            String title = m.name + " Info" + (isTM || isTR ? " (" + moveString.toUpperCase() + ")" : "");
            String info = m.flavor.isEmpty() ? "" : m.flavor.get(new Random().nextInt(m.flavor.size()));
            String type = "Type: " + Global.normalCase(m.type.toString());
            String category = "Category: " + Global.normalCase(m.category.toString());
            String power = "Power: " + m.basePower;
            String accuracy = "Accuracy: " + m.baseAccuracy;

            this.embed.setTitle(title);
            this.embed.setDescription(info + "\n\n" + type + "\n" + category + "\n" + power + "\n" + accuracy);
            this.color = m.type.getColor();

            if(Move.CUSTOM_MOVES.contains(moveString)) this.embed.setFooter("This move has a custom implementation! It may not work exactly as described!");
        }
        return this;
    }
}
