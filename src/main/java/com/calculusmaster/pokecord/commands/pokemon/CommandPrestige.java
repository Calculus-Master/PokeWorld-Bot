package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.util.enums.Prices;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandPrestige extends Command
{
    public CommandPrestige(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.PRESTIGE_POKEMON)) return this.invalidMasteryLevel(Feature.PRESTIGE_POKEMON);

        boolean info = this.msg.length == 1 || (this.msg.length == 2 && this.msg[1].equals("info"));
        boolean advance = this.msg.length == 2 && this.msg[1].equals("advance");

        Pokemon p = this.playerData.getSelectedPokemon();

        if(info)
        {
            this.embed
                    .setTitle("Prestige Info")
                    .setDescription("*Prestiging your Pokemon is a way to increase their power permanently.*")
                    .addField("Prestige", "Prestiging your Pokemon will reset its level to 1, remove its held items, TMs and TRs, and reset all of its moves. Before you Prestige, make sure to remove all Items from your Pokemon, or else they will be deleted!", false)
                    .addField("Prestige Bonuses", "Prestiging your Pokemon allows it to gain permanent boosts to its stats!", false)
                    .addField("Prestige Levels", "Prestiging your Pokemon increases its **Prestige Level**. Depending on its rarity, a Pokemon can prestige multiple times.", false)
                    .addField("Prestige Requirements", "- Your Pokemon must be Level 100\n - Your Pokemon must be under its Prestige Limit\n - Prestiging will cost %s credits\n\nIf your Pokemon is able to Prestige, use `p!prestige advance` to increase its power!".formatted(Prices.PRESTIGE.get()), false)
                    .setFooter(p.getLevel() == 100 && p.getPrestigeLevel() < p.getMaxPrestigeLevel() ? "Your " + p.getName() + " is currently able to Prestige!" : "Your " + p.getName() + " is not able to Prestige currently!");
        }
        else if(advance)
        {
            if(p.getLevel() < 100) this.response = "Your Pokemon must be Level 100 to Prestige!";
            else if(p.getPrestigeLevel() == p.getMaxPrestigeLevel()) this.response = p.getName() + " is already at its maximum Prestige Level!";
            else if(this.playerData.getCredits() < Prices.PRESTIGE.get()) this.invalidCredits(Prices.PRESTIGE.get());
            else
            {
                this.playerData.changeCredits(-1 * Prices.PRESTIGE.get());

                p.increasePrestigeLevel();
                p.updatePrestigeLevel();

                //Prestige Resets
                p.setLevel(1);
                p.setExp(0);
                p.removeItem();
                p.setTR();
                p.setTM();
                p.setMoves();

                p.completeUpdate();

                this.response = p.getName() + " successfully prestiged to **Prestige Level " + p.getPrestigeLevel() + "!**";
            }
        }
        else this.response = CommandInvalid.getShort();

        return this;
    }
}
