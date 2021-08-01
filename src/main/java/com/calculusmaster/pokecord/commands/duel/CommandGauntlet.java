package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.extension.GauntletDuel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandGauntlet extends Command
{
    public CommandGauntlet(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        boolean info = this.msg.length == 2 && this.msg[1].equals("info");
        boolean start = this.msg.length == 2 && this.msg[1].equals("start");

        if(info)
        {
            this.embed
                    .setTitle("Gauntlet Info")
                    .setDescription("Gauntlets are infinitely long Wild Pokemon duels that only stop once your selected Pokemon has fainted.")
                    .addField("Starting", "To start a Gauntlet, use `p!gauntlet start`. Your selected Pokemon will automatically be chosen to start the challenge.", false)
                    .addField("Dueling", "Once the Gauntlet has started, the duel is mechanically identical to a Wild Pokemon duel. However, if the Wild Pokemon is defeated, the duel will automatically progress to the next one.", false)
                    .addField("Level", "You are able to view the Gauntlet level you have most recently completed by using `p!gauntlet level`. The Level indicates how many Pokemon you have defeated, and determines the reward you will receive upon losing.", false)
                    .addField("Losing", "Losing a Gauntlet means that your selected Pokemon has fainted. You will then receive rewards based on how many levels you completed.", false)
                    .addField("Rewards", "Gauntlets offer credits and XP as rewards. EVs are not granted in Gauntlet duels, as they are intended as an endurance test.", false);
        }
        else if(start)
        {
            if(DuelHelper.isInDuel(this.player.getId())) this.sendMsg("You are already in a duel!");
            else
            {
                GauntletDuel gauntlet = GauntletDuel.start(this.player.getId(), this.event);

                this.sendMsg(this.playerData.getSelectedPokemon().getName() + " has started a Gauntlet!");

                gauntlet.sendTurnEmbed();
            }
        }
        else this.sendMsg(CommandInvalid.getShort());

        return this;
    }
}
