package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.Pokemon;
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
            String original = mega.substring("Mega ".length(), mega.contains(" X") || mega.contains(" Y") ? mega.length() - 2 : mega.length());

            selected.changeForm(original);
            Pokemon.uploadPokemon(selected);

            this.playerData.addPokePassExp(200, this.event);
            this.event.getChannel().sendMessage(this.playerData.getMention() + ": " + mega + " has de-evolved into " + original + "!").queue();
            this.embed = null;
        }
        else
        {
            this.embed.setDescription("Your Pokemon isn't a mega or primal!");
        }
        return this;
    }
}
