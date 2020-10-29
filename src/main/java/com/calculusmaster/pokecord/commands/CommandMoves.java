package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.game.MoveList;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.moves.Move;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandMoves extends Command
{
    public CommandMoves(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "moves");
    }

    @Override
    public Command runCommand()
    {
        Pokemon selected = this.playerData.getSelectedPokemon();

        StringBuilder movesList = new StringBuilder().append("**Learned Moves: **\n");
        for(int i = 0; i < 4; i++) movesList.append("Move " + (i + 1) + ": " + selected.getLearnedMoves().get(i) + "\n");

        movesList.append("\n**All Moves: **\n");
        for(String s : selected.getAllMoves()) movesList.append(s + (selected.getAvailableMoves().contains(s) || (Move.isMove(s) && Move.asMove(s).isWIP()) ? "" : " :lock: ") + "\n");

        //TODO: Add a WIP function so that moves that don't work are shown up as WIP
        this.embed.setDescription(movesList.toString());
        this.embed.setTitle("Level " + selected.getLevel() + " " + selected.getName());
        this.embed.setFooter("Moves with :lock: next to them are not yet unlocked! Unlock them by leveling up this Pokemon!");

        return this;
    }
}
