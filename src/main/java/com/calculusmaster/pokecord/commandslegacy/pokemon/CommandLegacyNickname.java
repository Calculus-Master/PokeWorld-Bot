package com.calculusmaster.pokecord.commandslegacy.pokemon;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandLegacyNickname extends CommandLegacy
{
    public CommandLegacyNickname(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public CommandLegacy runCommand()
    {
        Pokemon s = this.playerData.getSelectedPokemon();

        //Set new nickname
        if(this.msg.length >= 2)
        {
            this.msg = this.event.getMessage().getContentRaw().trim().split("\\s+");

            String nick = this.getMultiWordContent(1);
            s.setNickname(nick);

            this.response = "Changed your Pokemon's nickname to `" + nick + "`!";
        }
        //Reset nickname (delete it)
        else
        {
            s.setNickname("");

            this.response = "Reset your Pokemon's nickname!";
        }

        s.updateNickname();
        return this;
    }
}
