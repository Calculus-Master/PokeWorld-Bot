package com.calculusmaster.pokecord.commands.moves;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.MoveData;
import com.calculusmaster.pokecord.util.Global;
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
        if(this.insufficientMasteryLevel(Feature.VIEW_MOVE_INFO)) return this.invalidMasteryLevel(Feature.VIEW_MOVE_INFO);

        if(this.msg.length < 2) this.response = "Invalid arguments! You need to include a move name!";
        else
        {
            String input = Global.normalize(this.getMultiWordContent(1));

            if(!Move.isMove(input)) this.response = "`" + input + "` is not a valid Move name!";
            else
            {
                MoveData m = MoveData.get(input);

                String impl = Move.isImplemented(m.name) ? "" : "***Warning: Move is not implemented! You cannot use " + m.name + " in duels!***\n\n";
                String flavor = m.flavor.isEmpty() ? "*No Move Description*" : m.flavor.get(new Random().nextInt(m.flavor.size()));

                this.embed
                        .setDescription(impl + flavor)
                        .addField("Kind", m.isZMove ? "Z-Move" : (m.isMaxMove ? "Max Move" : "Regular Move"), true)
                        .addField("Type", Global.normalize(m.type.toString()), true)
                        .addField("Category", m.category != null ? Global.normalize(m.category.toString()) : "Mirrors Base Move", true)
                        .addField("Power", m.isZMove || m.isMaxMove ? "Depends on Base Move" : String.valueOf(m.basePower), true)
                        .addField("Accuracy", m.isZMove || m.isMaxMove ? "Always Hits" : String.valueOf(m.baseAccuracy), true)
                        .addBlankField(true);

                this.embed.setTitle(m.name + " Info");
                this.color = m.type.getColor();

                if(Move.CUSTOM_MOVES.contains(m.name)) this.embed.setFooter("This move has a custom implementation! It may not work exactly as described!");
            }
        }

        return this;
    }
}
