package com.calculusmaster.pokecord.commandslegacy.duel;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.commandslegacy.CommandLegacyInvalid;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.extension.ZTrialDuel;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Objects;

public class CommandLegacyZTrialDuel extends CommandLegacy
{
    public CommandLegacyZTrialDuel(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public CommandLegacy runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.PVE_DUELS_ZTRIAL)) return this.invalidMasteryLevel(Feature.PVE_DUELS_ZTRIAL);

        if(this.msg.length == 2 && Type.cast(this.msg[1]) != null)
        {
            Type type = Type.cast(this.msg[1]);
            ZCrystal crystal = Objects.requireNonNull(ZCrystal.getCrystalOfType(type));

            if(DuelHelper.isInDuel(this.player.getId())) this.response = CommandLegacyInvalid.ALREADY_IN_DUEL;
            else if(this.playerData.getInventory().hasZCrystal(crystal)) this.response = "You already have `" + crystal.getStyledName() + "`!";
            else if(!this.isEligibleForTrial(type)) this.response = "You are not ready for a Z Trial! You will need to have 50 " + type.getStyledName() + "-Type Pokemon and 2000 credits!";
            else
            {
                this.playerData.changeCredits(-2000);

                Duel d = ZTrialDuel.create(this.player.getId(), this.event, type);

                this.event.getChannel().sendMessage("A Trial Leader appears!").queue();

                this.embed = null;
                d.sendTurnEmbed();
            }
        }
        else this.response = "Invalid arguments! Make sure your Type name is correct.";

        return this;
    }

    private boolean isEligibleForTrial(Type type)
    {
        //50 Pokemon of Type
        //2000 Credits

        return this.playerData.getPokemon().stream().filter(p -> p.isType(type)).count() >= 50 && this.playerData.getCredits() >= 2000;
    }
}
