package com.calculusmaster.pokecord.commandslegacy.pokemon;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.commandslegacy.CommandLegacyInvalid;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.pokemon.evolution.PokemonEgg;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class CommandLegacyEggs extends CommandLegacy
{
    public CommandLegacyEggs(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public CommandLegacy runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.HATCH_EGGS)) return this.invalidMasteryLevel(Feature.HATCH_EGGS);

        //boolean info = this.msg.length == 1 ||  (this.msg.length == 2 && this.msg[1].equals("info"));
        boolean view = this.msg.length == 1 || (this.msg.length == 2 && this.msg[1].equals("view"));
        boolean equip = this.msg.length == 3 && this.msg[1].equals("equip") && this.isNumeric(2);

        if(view)
        {
            this.embed.setTitle(this.player.getName() + "'s Eggs");

            if(!this.playerData.hasEggs()) this.embed.setDescription("You do not have any eggs! Use p!breed to breed two of your Pokemon and receive an egg!");
            else
            {
                List<PokemonEgg> eggs = this.playerData.getOwnedEggs();

                for(int i = 0; i < eggs.size(); i++) this.embed.addField("Egg " + (i + 1), eggs.get(i).getOverview(), true);

                this.embed.addField("Active Egg", this.playerData.hasActiveEgg() ? this.playerData.getActiveEgg().getOverview() : "None", false);
            }
        }
        else if(equip)
        {
            int index = this.getInt(2);

            if(index < 1 || index > this.playerData.getOwnedEggIDs().size()) this.response = "Invalid index!";
            else if(this.playerData.getActiveEggID().equals(this.playerData.getOwnedEggIDs().get(index - 1))) this.response = "This egg is already your active one!";
            else
            {
                this.playerData.setActiveEgg(this.playerData.getOwnedEggIDs().get(index - 1));

                this.response = "Successfully set this egg as your active one!";
            }
        }
        else this.response = CommandLegacyInvalid.getShort();

        return this;
    }
}
