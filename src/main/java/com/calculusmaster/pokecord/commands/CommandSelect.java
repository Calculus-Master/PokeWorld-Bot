package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

public class CommandSelect extends Command
{
    public CommandSelect(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "select:pick <number>");
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length != 2 || !this.msg[1].chars().allMatch(Character::isDigit) || Integer.parseInt(this.msg[1]) > this.playerData.getPokemonList().length())
        {
            this.embed.setDescription(CommandInvalid.getShort());
        }
        else
        {
            int selected = Integer.parseInt(this.msg[1]);
            this.playerData.setSelected(selected);
            JSONObject pokemon = Pokemon.specificJSON(this.playerData.getPokemonList().getString(this.playerData.getSelected()));
            this.embed.setDescription("You selected your **Level " + pokemon.getInt("level") + " " + Global.normalCase(pokemon.getString("name")) + "** (#" + selected + ")!");
        }

        return this;
    }
}
