package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
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
        Pokemon selected = this.playerData.getSelectedPokemon();

        if(selected.getName().contains("Mega") || selected.getName().contains("Primal"))
        {
            String mega = selected.getName();
            String original = "";
            if(selected.getName().contains("Mega")) original = mega.substring("Mega ".length(), mega.contains(" X") || mega.contains(" Y") ? mega.length() - 2 : mega.length());
            else if(selected.getName().contains("Primal")) original = mega.substring("Primal ".length());

            selected.changeForm(original);

            this.event.getChannel().sendMessage(this.playerData.getMention() + ": " + mega + " has de-evolved into " + original + "!").queue();
            this.embed = null;
        }
        else
        {
            if(selected.getMegaList().size() == 1)
            {
                String mega = selected.getMegaList().get(0);

                if(this.playerData.getOwnedMegas().contains(mega))
                {
                    this.embed.setDescription(selected.getName() + " mega evolved into " + selected.getMegaList().get(0) + "!");
                    selected.changeForm(selected.getMegaList().get(0));
                }
                else this.embed.setDescription("You don't own this Mega-Evolved form!");
            }
            else if(selected.getMegaList().size() == 2)
            {
                if(this.msg.length != 2)
                {
                    this.embed.setDescription("You need to specific which Mega to evolve into! Either use p!mega x or p!mega y");
                }

                String chosenMega = selected.getMegaList().get(this.msg[1].equals("x") ? 0 : 1);

                if(this.playerData.getOwnedMegas().contains(chosenMega))
                {
                    this.embed.setDescription(selected.getName() + " mega evolved into " + chosenMega + "!");
                    selected.changeForm(chosenMega);
                }
                else this.embed.setDescription("You don't own this Mega-Evolved form!");
            }
            else
            {
                this.embed.setDescription("Either your Pokemon cannot mega evolve, or you do not own the Mega-Evolved Form!");
            }
        }

        return this;
    }
}
