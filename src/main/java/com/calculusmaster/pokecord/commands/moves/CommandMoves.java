package com.calculusmaster.pokecord.commands.moves;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandMoves extends Command
{
    public CommandMoves(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        Pokemon selected = this.playerData.getSelectedPokemon();
        boolean inDuel = DuelHelper.isInDuel(this.player.getId());
        StringBuilder movesList = new StringBuilder();

        if(inDuel)
        {
            Duel d = DuelHelper.instance(this.player.getId());
            selected = d.getPlayers()[d.indexOf(this.player.getId())].active;

            movesList.append("**Learned Moves: **\n");
            Move m;
            for(int i = 0; i < 4; i++)
            {
                movesList.append(i + 1).append(": ");

                m = new Move(selected.getLearnedMoves().get(i));

                if(selected.isDynamaxed()) movesList.append(DuelHelper.getMaxMove(selected, m).getName());
                else movesList.append(m.getName()).append(" (Max Move: ").append(DuelHelper.getMaxMove(selected, m).getName()).append(")");

                movesList.append("\n");
            }
        }

        if(!inDuel)
        {
            movesList.append("**Learned Moves: **\n");
            for(int i = 0; i < 4; i++) movesList.append("Move ").append(i + 1).append(": ").append(selected.getLearnedMoves().get(i)).append("\n");

            movesList.append("\n**All Moves: **\n");
            String emote;
            for (String s : selected.getAllMoves())
            {
                if(selected.getAvailableMoves().contains(s) && Move.isMove(s) && !Move.WIP_MOVES.contains(s)) emote = " ";
                else if(!selected.getAvailableMoves().contains(s) && Move.isMove(s) && !Move.WIP_MOVES.contains(s)) emote = " :lock:";
                else emote = " :no_entry_sign:";

                movesList.append(s).append(emote).append("\n");
            }
        }

        this.embed.setDescription(movesList.toString());
        this.embed.setTitle("Level " + selected.getLevel() + " " + selected.getName());
        if(!inDuel) this.embed.setFooter(":lock: signifies moves that are either not unlocked or not implemented! Unlock them by leveling up this Pokemon!");

        this.playerData.addPokePassExp(50, this.event);
        return this;
    }
}
