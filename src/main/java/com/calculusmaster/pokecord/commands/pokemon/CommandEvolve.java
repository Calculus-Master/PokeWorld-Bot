package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandEvolve extends Command
{
    public CommandEvolve(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        Pokemon selected = this.playerData.getSelectedPokemon();
        this.embed = null;

        if(selected.canEvolve())
        {
            String old = selected.getName();
            selected.evolve();

            this.playerData.addPokePassExp(500, this.event);
            this.playerData.updateBountyProgression(ObjectiveType.EVOLVE_POKEMON);

            this.event.getChannel().sendMessage(this.playerData.getMention() + ": Your " + old + " evolved into a " + selected.getName() + "!").queue();
        }
        else this.event.getChannel().sendMessage(this.playerData.getMention() + ": Your " + selected.getName() + " cannot evolve right now!").queue();

        return this;
    }
}
