package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.pokemon.MegaChargeManager;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandMega extends Command
{
    public CommandMega(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.ACQUIRE_POKEMON_MEGA_EVOLUTIONS)) return this.invalidMasteryLevel(Feature.ACQUIRE_POKEMON_MEGA_EVOLUTIONS);

        Pokemon selected = this.playerData.getSelectedPokemon();

        if(selected.getName().contains("Mega") || selected.getName().contains("Primal"))
        {
            String mega = selected.getName();
            selected.removeMegaEvolution();

            MegaChargeManager.removeBlocking(selected.getUUID());

            this.response = mega + " has de-evolved into " + selected.getName() + "!";
        }
        else
        {
            if(selected.getMegaList().size() > 0 && selected.getMegaCharges() == 0)
                this.response = selected.getName() + " cannot Mega-Evolve! It has no Mega Charges available currently.";
            else if(selected.getMegaList().size() == 1)
            {
                String mega = selected.getMegaList().get(0);

                if(this.playerData.getOwnedMegas().contains(mega))
                {
                    this.embed.setDescription(selected.getName() + " mega evolved into " + selected.getMegaList().get(0) + "!");

                    selected.changeForm(selected.getMegaList().get(0));
                    selected.updateName();

                    MegaChargeManager.blockCharging(selected.getUUID());
                }
                else this.embed.setDescription("You don't own this Mega Evolution!");
            }
            else if(selected.getMegaList().size() == 2)
            {
                if(this.msg.length != 2)
                {
                    this.embed.setDescription("You need to specific which Mega to evolve into! Either use p!mega x or p!mega y");
                    return this;
                }

                String chosenMega = selected.getMegaList().get(this.msg[1].equals("x") ? 0 : 1);

                if(this.playerData.getOwnedMegas().contains(chosenMega))
                {
                    this.embed.setDescription(selected.getName() + " mega evolved into " + chosenMega + "!");

                    selected.changeForm(chosenMega);
                    selected.updateName();

                    MegaChargeManager.blockCharging(selected.getUUID());
                }
                else this.embed.setDescription("You don't own this Mega Evolution!");
            }
            else
            {
                this.embed.setDescription("Either your Pokemon cannot mega evolve, or you do not own the Mega Evolution!");
            }
        }

        return this;
    }
}
