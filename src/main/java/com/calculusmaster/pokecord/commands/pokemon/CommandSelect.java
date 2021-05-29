package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

public class CommandSelect extends Command
{
    public CommandSelect(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length != 2 || (!this.msg[1].equals("latest") && (!this.isNumeric(1) || Integer.parseInt(this.msg[1]) > this.playerData.getPokemonList().length())))
        {
            this.embed.setDescription(CommandInvalid.getShort());
        }
        else
        {
            int selected = this.msg[1].equals("latest") ? this.playerData.getPokemonList().length() : this.getInt(1);
            this.playerData.setSelected(selected);
            Pokemon p = this.playerData.getSelectedPokemon();

            this.embed = null;
            this.event.getChannel().sendMessage("You selected your **Level " + p.getLevel() + " " + p.getName() + "** (#" + selected + ")!").queue();
        }

        return this;
    }
}
