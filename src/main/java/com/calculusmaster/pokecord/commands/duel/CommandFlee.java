package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.duel.TrainerDuel;
import com.calculusmaster.pokecord.game.duel.WildDuel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandFlee extends Command
{
    public CommandFlee(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(!DuelHelper.isInDuel(this.player.getId()))
        {
            this.sendMsg("You aren't in a duel!");
        }
        else
        {
            Duel d = DuelHelper.instance(this.player.getId());

            if(!(d instanceof WildDuel))
            {
                this.sendMsg(d instanceof TrainerDuel ? "Use p!concede to leave a Trainer Duel!" : "You cannot flee from this duel!");
            }
            else
            {
                //TODO: Random chance of fleeing
                //TODO: p!concede
                Pokemon.uploadPokemon(d.getPlayers()[0].active);

                DuelHelper.delete(this.player.getId());

                this.sendMsg("Successfully fled from the Wild Pokemon!");
            }
        }

        return this;
    }
}
