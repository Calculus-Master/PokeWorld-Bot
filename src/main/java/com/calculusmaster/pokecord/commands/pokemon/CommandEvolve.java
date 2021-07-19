package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.SpecialEvolutionRegistry;
import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;

public class CommandEvolve extends Command
{
    public CommandEvolve(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(DuelHelper.isInDuel(this.player.getId()))
        {
            this.sendMsg("You cannot evolve Pokemon while in a duel!");
            return this;
        }

        Pokemon selected = this.playerData.getSelectedPokemon();

        boolean normal = selected.getData().evolutions.size() > 0 && selected.getLevel() >= new ArrayList<>(selected.getData().evolutions.values()).get(0);
        boolean special = SpecialEvolutionRegistry.hasSpecialEvolution(selected.getName()) && SpecialEvolutionRegistry.canEvolve(selected);

        String target = "";

        if(special) target = SpecialEvolutionRegistry.getTarget(selected);

        if(!special && normal) target = new ArrayList<>(selected.getData().evolutions.keySet()).get(0);

        if(!target.equals(""))
        {
            Pokemon.updateName(selected, target);

            this.playerData.addPokePassExp(500, this.event);
            this.playerData.updateBountyProgression(ObjectiveType.EVOLVE_POKEMON);

            this.sendMsg("`" + selected.getName() + "` evolved into `" + target + "`!");

        }
        else this.sendMsg(selected.getName() + " cannot evolve right now!");

        return this;
    }
}
