package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.extension.ZTrialDuel;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.player.level.PlayerLevel;
import com.calculusmaster.pokecord.util.helpers.CacheHelper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Objects;

public class CommandZTrialDuel extends Command
{
    public CommandZTrialDuel(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 2 && Type.cast(this.msg[1]) != null)
        {
            Type type = Type.cast(this.msg[1]);
            ZCrystal crystal = Objects.requireNonNull(ZCrystal.getCrystalOfType(type));

            if(DuelHelper.isInDuel(this.player.getId())) this.response = CommandInvalid.ALREADY_IN_DUEL;
            else if(this.playerData.hasZCrystal(crystal.toString())) this.response = "You already have `" + crystal.getStyledName() + "`!";
            else if(!this.isEligibleForTrial(type)) this.response = "You are not ready for a Z Trial! You will need to have 50 " + type.getStyledName() + " Pokemon, 2000 credits, and be Pokemon Mastery Level 4 to challenge a Trial!";
            else
            {
                this.playerData.changeCredits(-1 * 2000);

                Duel d = ZTrialDuel.create(this.player.getId(), this.event, type);

                this.response = "A Trial Leader appears!";

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
        //Pokemon Mastery Level 4

        return CacheHelper.POKEMON_LISTS.get(this.player.getId()).stream().filter(p -> p.isType(type)).count() >= 50
                && this.playerData.getCredits() >= 2000
                && this.playerData.getLevel() > PlayerLevel.REQUIRED_LEVEL_Z_TRIAL;
    }
}
