package com.calculusmaster.pokecord.commands.moves;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

public class CommandMoveInfo extends Command
{
    public CommandMoveInfo(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "moveinfo <move>");
    }

    @Override
    public Command runCommand()
    {
        //TODO: Add in info for TM and TR that integrates with this command
        if(this.msg.length < 2)
        {
            this.embed.setDescription(CommandInvalid.getShort());
        }
        else
        {
            String moveString = Global.normalCase(this.getMove());
            System.out.println(moveString);

            if(!Move.isMove(moveString))
            {
                this.embed.setDescription(CommandInvalid.getShort());
                return this;
            }

            Move m = Move.asMove(moveString);

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

            if(Move.asMove(moveString).isCustom()) this.embed.setFooter("This move has a custom implementation! It may not work exactly as described!");
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
