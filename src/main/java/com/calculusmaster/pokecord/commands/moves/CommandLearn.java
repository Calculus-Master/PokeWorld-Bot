package com.calculusmaster.pokecord.commands.moves;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.commands.Commands;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CommandLearn extends Command
{
    public static Map<String, MoveEntity> moveLearnRequests = new HashMap<>();

    public CommandLearn(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.LEARN_REPLACE_MOVES)) return this.invalidMasteryLevel(Feature.LEARN_REPLACE_MOVES);

        if(this.msg.length < 2)
        {
            this.response = CommandInvalid.getShort();
            return this;
        }

        Pokemon selected = this.playerData.getSelectedPokemon();
        MoveEntity move = MoveEntity.cast(Global.normalize(this.getMultiWordContent(1)));

        boolean autoReplace = false;
        if(this.isNumeric(1) && this.msg.length > 2 && this.getInt(1) > 0 && this.getInt(1) < 5)
        {
            move = MoveEntity.cast(Global.normalize(this.getMultiWordContent(2)));
            autoReplace = true;
        }

        if(move == null) this.response = "Invalid move name!";
        else if(!Move.isImplemented(move)) this.response = "`" + move + "` has not been implemented yet!";
        else if(!selected.availableMoves().contains(move)) this.response = selected.getName() + " does not know `" + move + "`";
        else
        {
            StringBuilder movesList = new StringBuilder().append("\n");
            for(int i = 0; i < 4; i++) movesList.append(i + 1).append(": ").append(selected.getMoves().get(i)).append("\n");

            this.embed.setDescription("Which move do you want to replace with " + move + "?" + movesList);
            moveLearnRequests.put(selected.getUUID(), move);

            if(autoReplace)
            {
                this.response = "Learning move...";
                this.embed = null;
                Executors.newSingleThreadScheduledExecutor().schedule(() -> Commands.execute("replace", this.event, new String[]{"replace", "" + this.getInt(1)}), 450, TimeUnit.MILLISECONDS);
            }
        }

        return this;
    }
}
