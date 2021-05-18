package com.calculusmaster.pokecord.commands.moves;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
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
        for(String s : selected.getAllMoves()) movesList.append(s + (selected.getAvailableMoves().contains(s) && (Move.isMove(s) && !Move.WIP_MOVES.contains(s)) ? "" : " :lock: ") + "\n");

        this.embed.setDescription(movesList.toString());
        this.embed.setTitle("Level " + selected.getLevel() + " " + selected.getName());
        this.embed.setFooter("Moves with :lock: next to them are either not yet unlocked, WIP, or not implemented! Unlock them by leveling up this Pokemon!");

        return this;
    }
}
