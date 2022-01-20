package com.calculusmaster.pokecord.commands.moves;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandReplace extends Command
{
    public CommandReplace(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.LEARN_REPLACE_MOVES)) return this.invalidMasteryLevel(Feature.LEARN_REPLACE_MOVES);

        if(this.msg.length != 2 || !this.msg[1].chars().allMatch(Character::isDigit))
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        Pokemon selected = this.playerData.getSelectedPokemon();

        if(!CommandLearn.moveLearnRequests.containsKey(selected.getUUID()))
        {
            this.embed.setDescription("You need to specify a move for " + selected.getName() + " to learn!");
        }
        else
        {
            int index = Integer.parseInt(this.msg[1]) - 1;
            String oldMove = selected.getMoves().get(index);
            String newMove = CommandLearn.moveLearnRequests.get(selected.getUUID());

            selected.learnMove(newMove, index);
            CommandLearn.moveLearnRequests.remove(selected.getUUID(), newMove);
            selected.updateMoves();

            this.embed.setDescription("Replaced " + oldMove + " with " + newMove + "!");
            this.color = new Move(newMove).getType().getColor();
        }

        return this;
    }
}
