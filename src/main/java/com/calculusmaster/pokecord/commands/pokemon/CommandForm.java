package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandForm extends Command
{
    public CommandForm(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 1)
        {
            this.embed.setDescription("You need to include the form name!");
            return this;
        }

        String form = this.getForm();
        Pokemon s = this.playerData.getSelectedPokemon();

        if(!Global.POKEMON.contains(form))
        {
            this.embed.setDescription("That is not a valid form!");
            return this;
        }
        else if(!this.playerData.getOwnedForms().contains(form))
        {
            this.embed.setDescription("You don't own that form!");
            return this;
        }
        else if(!s.getFormsList().contains(form))
        {
            this.embed.setDescription(s.getName() + " cannot transform into " + form);
            return this;
        }
        else
        {
            this.embed.setDescription(s.getName() + " transformed into " + form + "!");
            s.changeForm(form);
            return this;
        }
    }

    private String getForm()
    {
        StringBuilder form = new StringBuilder();
        for(int i = 1; i < this.msg.length; i++) form.append(this.msg[i]).append(" ");

        return Global.normalCase(form.toString().trim());
    }
}
