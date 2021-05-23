package com.calculusmaster.pokecord.commands.moves;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
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

        if((tm && number > 0 && number <= 100 && this.JSONArrayContains(this.playerData.getOwnedTMs(), TM.get(number).toString())) || (!tm && number >= 0 && number < 100 && this.JSONArrayContains(this.playerData.getOwnedTRs(), TR.get(number).toString())))
        {
            Pokemon selected = this.playerData.getSelectedPokemon();
            int held;

            if(tm && selected.canLearnTM(number))
            {
                held = selected.getTM();
                selected.setTM(number);
                Pokemon.updateTMTR(selected);

                this.embed.setDescription("Taught " + TM.get(number).toString() + " - " + TM.get(number).getMoveName() + " to " + selected.getName());

                this.playerData.removeTM(TM.get(number).toString());
                if(held != -1) this.playerData.addTM(TM.get(held).toString());
            }
            else if(!tm && selected.canLearnTR(number))
            {
                held = selected.getTR();
                selected.setTR(number);
                Pokemon.updateTMTR(selected);

                this.embed.setDescription("Taught " + TR.get(number).toString() + " - " + TR.get(number).getMoveName() + " to " + selected.getName());

                this.playerData.removeTR(TR.get(number).toString());
                if(held != -1) this.playerData.addTR(TR.get(held).toString());
            }
            else this.embed.setDescription(selected.getName() + " cannot learn that " + (tm ? "TM" : "TR") + "!");
        }

        return this;
    }

    private boolean JSONArrayContains(JSONArray j, String val)
    {
        for (int i = 0; i < j.length(); i++) if(j.getString(i).equals(val)) return true;
        return false;
    }
}
