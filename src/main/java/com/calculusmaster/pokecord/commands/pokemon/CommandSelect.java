package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.tournament.Tournament;
import com.calculusmaster.pokecord.game.tournament.TournamentHelper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandSelect extends Command
{
    public CommandSelect(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(TournamentHelper.isInTournament(this.player.getId()))
        {
            Tournament t = TournamentHelper.instance(this.player.getId());

            if(t.getSize() == 1 && t.getStatus().equals(TournamentHelper.TournamentStatus.DUELING) && !t.isPlayerEliminated(this.player.getId()))
            {
                this.sendMsg("You can't change your selected Pokemon while in a Tournament!");
                return this;
            }
        }

        if(this.msg.length != 2 || (!this.msg[1].equals("latest") && (!this.isNumeric(1) || Integer.parseInt(this.msg[1]) > this.playerData.getPokemonList().size())))
        {
            this.embed.setDescription(CommandInvalid.getShort());
        }
        else
        {
            int selected = this.msg[1].equals("latest") ? this.playerData.getPokemonList().size() : this.getInt(1);
            this.playerData.setSelected(selected);
            Pokemon p = this.playerData.getSelectedPokemon();

            this.embed = null;
            this.event.getChannel().sendMessage("You selected your **Level " + p.getLevel() + " " + p.getName() + "** (#" + selected + ")!").queue();
        }

        return this;
    }
}
