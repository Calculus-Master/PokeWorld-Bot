package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
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
        boolean latest = this.msg.length == 2 && this.msg[1].equals("latest");
        boolean number = this.msg.length == 2 && this.isNumeric(1);

        if(TournamentHelper.isInTournament(this.player.getId()))
        {
            Tournament t = TournamentHelper.instance(this.player.getId());

            if(t.getSize() == 1 && t.getStatus().equals(TournamentHelper.TournamentStatus.DUELING) && !t.isPlayerEliminated(this.player.getId()))
            {
                this.response = "You can't change your selected Pokemon while in a Tournament!";
                return this;
            }
        }

        if(number || latest)
        {
            if(number && this.getInt(1) > this.playerData.getPokemonList().size()) this.response = "That number exceeds the amount of Pokemon you have!";
            else
            {
                int newSelected = latest ? this.playerData.getPokemonList().size() : this.getInt(1);

                this.playerData.setSelected(newSelected);

                Pokemon p = this.playerData.getSelectedPokemon();
                this.response = "You selected your **Level " + p.getLevel() + " " + p.getName() + "** (#" + newSelected + ")!";
            }
        }
        else this.response = CommandInvalid.getShort();

        return this;
    }
}
