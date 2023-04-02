package com.calculusmaster.pokecord.commandslegacy.moves;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.commandslegacy.CommandLegacyInvalid;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandLegacyReplace extends CommandLegacy
{
    public CommandLegacyReplace(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public CommandLegacy runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.LEARN_REPLACE_MOVES)) return this.invalidMasteryLevel(Feature.LEARN_REPLACE_MOVES);

        if(this.msg.length != 2 || !this.msg[1].chars().allMatch(Character::isDigit))
        {
            this.embed.setDescription(CommandLegacyInvalid.getShort());
            return this;
        }

        Pokemon selected = this.playerData.getSelectedPokemon();

        if(!CommandLegacyLearn.moveLearnRequests.containsKey(selected.getUUID()))
        {
            this.embed.setDescription("You need to specify a move for " + selected.getName() + " to learn!");
        }
        else
        {
            int index = Integer.parseInt(this.msg[1]) - 1;
            MoveEntity oldMove = selected.getMoves().get(index);
            MoveEntity newMove = CommandLegacyLearn.moveLearnRequests.get(selected.getUUID());

            selected.learnMove(newMove, index);
            CommandLegacyLearn.moveLearnRequests.remove(selected.getUUID(), newMove);
            selected.updateMoves();

            this.embed.setDescription("Replaced " + oldMove.data().getName() + " with " + newMove.data().getName() + "!");
            this.color = new Move(newMove).getType().getColor();
        }

        return this;
    }
}
