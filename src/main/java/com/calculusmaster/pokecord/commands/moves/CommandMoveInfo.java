package com.calculusmaster.pokecord.commands.moves;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandMoveInfo extends Command
{
    public CommandMoveInfo(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "moveinfo <move>");
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
            String moveString = Global.normalCase(this.getMove());
            System.out.println(moveString);
            boolean isTM = moveString.startsWith("Tm");
            boolean isTR = moveString.startsWith("Tr");

            if(!Move.isMove(moveString) && !(isTM || isTR))
            {
                this.embed.setDescription(CommandInvalid.getShort());
                return this;
            }

            Move.MoveData m = isTM ? TM.get(Integer.parseInt(moveString.substring(2))).getMoveData() : (isTR ? TR.get(Integer.parseInt(moveString.substring(2))).getMoveData() : Move.MOVES.get(moveString));

            String title = m.name + " Info" + (isTM || isTR ? " (" + moveString.toUpperCase() + ")" : "");
            String info = m.info;
            String type = "Type: " + Global.normalCase(m.type.toString());
            String category = "Category: " + Global.normalCase(m.category.toString());
            String power = "Power: " + m.power;
            String accuracy = "Accuracy: " + m.accuracy;
            String zmove = "Z-Move: " + m.zmove;

            this.embed.setTitle(title);
            this.embed.setDescription(info + "\n\n" + type + "\n" + category + "\n" + power + "\n" + accuracy + "\n" + zmove);
            this.color = m.type.getColor();

            if(Move.CUSTOM_MOVES.contains(moveString)) this.embed.setFooter("This move has a custom implementation! It may not work exactly as described!");
        }
        return this;
    }

    private String getMove()
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < this.msg.length; i++) sb.append(this.msg[i]).append(" ");
        return sb.toString().trim();
    }
}
