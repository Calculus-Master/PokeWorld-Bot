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
        if(this.msg.length != 2 || !Move.isMove(this.msg[1]))
        {
            this.embed.setDescription(CommandInvalid.getShort());
        }
        else
        {
            JSONObject move = new JSONObject(Mongo.MoveInfo.find(Filters.eq("name", Global.normalCase(this.msg[1]))).first().toJson());

            String title = Global.normalCase(this.msg[1]) + " Info";
            String info = move.getString("info");
            String type = "Type: " + move.getString("type");
            String category = "Category: " + move.getString("category");
            String power = "Power: " + move.getInt("power");
            String accuracy = "Accuracy: " + move.getInt("accuracy");
            String zmove = "Z-Move: " + move.getString("zmove");

            this.embed.setTitle(title);
            this.embed.setDescription(info + "\n\n" + type + "\n" + category + "\n" + power + "\n" + accuracy + "\n" + zmove);
            this.color = Type.cast(move.getString("type")).getColor();
        }
        return this;
    }
}
