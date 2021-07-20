package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.duel.TrainerDuel;
import com.calculusmaster.pokecord.game.duel.WildDuel;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandFlee extends Command
{
    private static final List<String> concedeRequests = new ArrayList<>();

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

            if(this.msg[0].equals("flee") || this.msg[0].equals("run"))
            {
                //p!flee or p!run
                if(!(d instanceof WildDuel))
                {
                    this.sendMsg(d instanceof TrainerDuel ? "Use p!concede to leave a Trainer Duel!" : "You cannot flee from this duel!");
                }
                else
                {
                    //TODO: Random chance of fleeing
                    Pokemon.uploadPokemon(d.getPlayers()[0].active);

                    DuelHelper.delete(this.player.getId());

                    this.sendMsg("Successfully fled from the Wild Pokemon!");
                }
            }
            else
            {
                //p!concede, p!concede confirm or p!concede deny
                if(d instanceof TrainerDuel)
                {
                    if(!concedeRequests.contains(this.player.getId()) || this.msg.length == 1)
                    {
                        concedeRequests.add(this.player.getId());

                        this.sendMsg("Are you sure you want to concede from this duel? You will lose 500c! (Type `p!concede confirm` or `p!concede deny` to continue)");
                    }
                    else if(concedeRequests.contains(this.player.getId()) && this.msg.length >= 2)
                    {
                        if(this.msg[1].equals("confirm"))
                        {
                            concedeRequests.remove(this.player.getId());

                            int loss = Math.min(this.playerData.getCredits(), 500);
                            this.playerData.changeCredits(-1 * loss);

                            DuelHelper.delete(this.player.getId());

                            this.sendMsg("You conceded and lost " + loss + "c!");
                        }
                        else if(this.msg[1].equals("deny"))
                        {
                            concedeRequests.remove(this.player.getId());

                            this.sendMsg("You chose not to concede! The duel will continue.");
                        }
                        else this.sendMsg("Type either `p!concede confirm` or `p!concede deny` to continue!");
                    }
                }
                else
                {
                    this.sendMsg(d instanceof WildDuel ? "Use p!run to flee from a Wild Pokemon Duel!" : "You cannot flee from this duel!");
                }
            }
        }

        return this;
    }
}
