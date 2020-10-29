package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.game.moves.Move;
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
        if(this.msg.length != 2 || !Move.isMove(this.msg[1]))
        {
            this.embed.setDescription(CommandInvalid.getShort());
        }
        else
        {
            Move m = Move.asMove(this.msg[1]);

            String title = m.getName() + " Info";
            String info = m.getInfo();
            String type = "Type: " + Global.normalCase(m.getType().toString());
            String category = "Category: " + Global.normalCase(m.getCategory().toString());
            String power = "Power: " + m.getPower();
            String accuracy = "Accuracy: " + m.getAccuracy();
            String zmove = "Z-Move: " + m.getZMove();

            this.embed.setTitle(title);
            this.embed.setDescription(info + "\n\n" + type + "\n" + category + "\n" + power + "\n" + accuracy + "\n" + zmove);
            this.color = m.getType().getColor();
        }
        return this;
    }
}
