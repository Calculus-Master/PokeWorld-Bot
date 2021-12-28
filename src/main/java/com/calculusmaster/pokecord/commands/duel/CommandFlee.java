package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.extension.TrainerDuel;
import com.calculusmaster.pokecord.game.duel.extension.WildDuel;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        if(this.insufficientMasteryLevel(Feature.FLEE_TRAINER_DUELS)) return this.invalidMasteryLevel(Feature.FLEE_TRAINER_DUELS);

        if(!DuelHelper.isInDuel(this.player.getId())) this.response = "You aren't in a duel!";
        else
        {
            Duel d = DuelHelper.instance(this.player.getId());

            if(this.msg[0].equals("flee") || this.msg[0].equals("run"))
            {
                //p!flee or p!run
                if(!(d instanceof WildDuel)) this.response = d instanceof TrainerDuel ? "Use p!concede to leave a Trainer Duel!" : "You cannot flee from this duel!";
                else
                {
                    if(new Random().nextInt(100) < 66)
                    {
                        DuelHelper.delete(this.player.getId());

                        this.response = "Successfully fled from the Wild Pokemon!";
                    }
                    else
                    {
                        this.response = "You could not escape from the Wild Pokemon!";

                        d.submitMove(this.player.getId(), -1, 'i');
                        d.checkReady();
                    }
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

                        this.response = "Are you sure you want to concede from this duel? You will lose 500c! (Type `p!concede confirm` or `p!concede deny` to continue)";
                    }
                    else if(concedeRequests.contains(this.player.getId()) && this.msg.length >= 2)
                    {
                        if(this.msg[1].equals("confirm"))
                        {
                            concedeRequests.remove(this.player.getId());

                            int loss = Math.min(this.playerData.getCredits(), 500);
                            this.playerData.changeCredits(-1 * loss);

                            DuelHelper.delete(this.player.getId());

                            this.response = "You conceded and lost " + loss + "c!";
                        }
                        else if(this.msg[1].equals("deny"))
                        {
                            concedeRequests.remove(this.player.getId());

                            this.response = "You chose not to concede! The duel will continue.";
                        }
                        else this.response = "Type either `p!concede confirm` or `p!concede deny` to continue!";
                    }
                }
                else this.response = d instanceof WildDuel ? "Use p!run to flee from a Wild Pokemon Duel!" : "You cannot flee from this duel!";
            }
        }

        return this;
    }
}
