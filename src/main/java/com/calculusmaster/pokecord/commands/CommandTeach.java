package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONArray;

public class CommandTeach extends Command
{
    public CommandTeach(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "p!teach <tm:tr> <number>");
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length < 3 || (!this.msg[1].equals("tr") && !this.msg[1].equals("tm")) || !this.msg[2].chars().allMatch(Character::isDigit))
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        boolean tm = this.msg[1].equals("tm");
        int number = Integer.parseInt(this.msg[2]);

        if((tm && number > 0 && number <= 100 && this.JSONArrayContains(this.playerData.getOwnedTMs(), TM.getTM(number).toString())) || (!tm && number >= 0 && number < 100 && this.JSONArrayContains(this.playerData.getOwnedTRs(), TR.getTR(number).toString())))
        {
            Pokemon selected = this.playerData.getSelectedPokemon();
            int held;

            if(tm && selected.canLearnTM(number))
            {
                held = selected.getTM();
                selected.setTM(number);
                Pokemon.updateTMTR(selected);

                this.embed.setDescription("Taught " + TM.getTM(number).toString() + " - " + TM.getTM(number).getMoveName() + " to " + selected.getName());

                this.playerData.removeTM(TM.getTM(number).toString());
                if(held != -1) this.playerData.addTM(TM.getTM(held).toString());
            }
            else if(!tm && selected.canLearnTR(number))
            {
                held = selected.getTR();
                selected.setTR(number);
                Pokemon.updateTMTR(selected);

                this.embed.setDescription("Taught " + TR.getTR(held).toString() + " - " + TR.getTR(held).getMoveName() + " to " + selected.getName());

                this.playerData.removeTR(TR.getTR(number).toString());
                if(held != -1) this.playerData.addTR(TR.getTR(held).toString());
            }
        }

        return this;
    }

    private boolean JSONArrayContains(JSONArray j, String val)
    {
        for (int i = 0; i < j.length(); i++) if(j.getString(i).equals(val)) return true;
        return false;
    }
}
