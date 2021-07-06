package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.Pokemon;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandNickname extends Command
{
    public CommandNickname(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        Pokemon s = this.playerData.getSelectedPokemon();

        //Set new nickname
        if(this.msg.length >= 2)
        {
            String nick = this.getMultiWordContent(2);
            Pokemon.updateNickname(s, nick);

            this.sendMsg("Changed your Pokemon's nickname to `" + nick + "`!");
        }
        //Reset nickname (delete it)
        else
        {
            Pokemon.updateNickname(s, "");

            this.sendMsg("Reset your Pokemon's nickname!");
        }

        return this;
    }
}
