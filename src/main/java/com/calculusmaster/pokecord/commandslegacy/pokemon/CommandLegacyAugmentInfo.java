package com.calculusmaster.pokecord.commandslegacy.pokemon;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugment;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugmentRegistry;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandLegacyAugmentInfo extends CommandLegacy
{
    public CommandLegacyAugmentInfo(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public CommandLegacy runCommand()
    {
        boolean available = this.msg.length == 2 && this.isNumeric(1);
        boolean equipped = this.msg.length == 3 && this.msg[1].equals("active") && this.isNumeric(2);

        if(available || equipped)
        {
            Pokemon active = this.playerData.getSelectedPokemon();

            List<PokemonAugment> augmentList = equipped ? new ArrayList<>(active.getAugments()) : PokemonAugmentRegistry.AUGMENT_DATA.get(active.getEntity()).getOrderedAugmentList();
            int index = this.getInt(equipped ? 2 : 1) - 1;

            if(index < 0 || index >= augmentList.size()) this.response = "Invalid number!";
            else
            {
                PokemonAugment augment = augmentList.get(index);

                this.embed
                        .setTitle(augment.getAugmentName())
                        .setDescription("*" + augment.getAugmentDescription() + "*")
                        .addField("Source", augment.getSource(), false)
                        .addField("Slots", String.valueOf(augment.getSlotCost()), true)
                        .addField("ID", augment.getAugmentID(), true)
                        .setFooter(this.playerData.isAugmentUnlocked(augment.getAugmentID()) ? "You have unlocked this Augment!" : "You have not unlocked this Augment!");
            }
        }
        else return this.invalid();

        return this;
    }
}
