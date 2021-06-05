package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.PokePass;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandPokePass extends Command
{
    public CommandPokePass(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("Current Tier: ").append(this.playerData.getPokePassTier()).append("\n");
        sb.append("Current EXP: ").append(this.playerData.getPokePassExp()).append(" / ").append(PokePass.TIER_EXP).append("\n\n");

        int start = Math.max(this.playerData.getPokePassTier() - 1, 0);
        int end = Math.min(start + 10, 100);

        for(int i = start; i < end; i++)
        {
            sb.append("**Tier ").append(i).append("**: ");

            if(!PokePass.tierExists(i)) sb.append("No Reward! Tier has not been added yet!");
            else sb.append(PokePass.getTierDescription(i)).append("\n");
        }

        this.embed.setDescription(sb.toString());
        this.embed.setTitle("PokePass");
        this.embed.setFooter("Showing Tiers " + start + " to " + end + "!");
        return this;
    }
}
