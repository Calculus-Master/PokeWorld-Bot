package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.game.Pokemon;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandReplace extends Command
{
    public CommandReplace(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "replace <number>");
    }

    @Override
    public Command runCommand()
    {
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
            int index = Integer.parseInt(this.msg[1]);
            String oldMove = selected.getLearnedMoves().get(index - 1);
            String newMove = CommandLearn.moveLearnRequests.get(selected.getUUID());

            selected.learnMove(newMove, index);
            CommandLearn.moveLearnRequests.remove(selected.getUUID(), newMove);
            Pokemon.updateMoves(selected);

            this.embed.setDescription("Replaced " + oldMove + " with " + newMove + "!");
            //this.color = Move.asMove(newMove).getType().getColor();
        }

        return this;
    }
}
