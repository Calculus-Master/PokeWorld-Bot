package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugment;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugmentRegistry;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandAugments extends Command
{
    public CommandAugments(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.AUGMENT_POKEMON)) return this.invalidMasteryLevel(Feature.AUGMENT_POKEMON);

        boolean info = this.msg.length == 2 && this.msg[1].equals("info");
        boolean equip = this.msg.length == 3 && this.msg[1].equals("equip") && this.isNumeric(2);
        boolean remove = this.msg.length == 3 && this.msg[1].equals("remove") && this.isNumeric(2);
        boolean clear = this.msg.length == 2 && this.msg[1].equals("clear");

        if(info)
        {
            this.embed
                    .setTitle("Augments Info")
                    .setDescription("Augments can strengthen your Pokemon.")
                    .addField("Augment Slots", "Each Pokemon has a limited number of Augment slots. Each Augment takes up a certain number of slots. You cannot equip Augments that exceed the total number of slots the Pokemon has. Pokemon will earn more slots by leveling up.", false)
                    .addField("Unlocking Augments", "Each Pokemon has a predetermined set of Augments that can be equipped. You can view these using the `p!pokemon augments` command. Each augment requires a minimum level to equip.", false)
                    .addField("Purchasing Augments", "The first time you equip an Augment, it will cost some credits. Once purchased once that Augment can be equipped for free on any Pokemon! Augments you own will be designated when viewing your Pokemon's augments.", false)
                    .addField("Important Note!", "Whenever you change your Pokemon's form, evolve your Pokemon, or purchase a Pokemon from the market, any equipped Augments will be automatically cleared.", false);
        }
        else if(equip)
        {
            Pokemon active = this.playerData.getSelectedPokemon();
            List<PokemonAugment> available = PokemonAugmentRegistry.AUGMENT_DATA.get(active.getName()).getOrderedAugmentList();
            int index = this.getInt(2) - 1;

            if(index < 0 || index >= available.size())
            {
                this.response = "Invalid number!";
                return this;
            }

            PokemonAugment augment = available.get(index);
            boolean owned = this.playerData.ownsAugment(augment.getAugmentID());
            int price = augment.getCreditCost();

            if(DuelHelper.isInDuel(this.player.getId())) this.response = "You cannot change Augments while in a Duel!";
            else if(!owned && this.playerData.getCredits() < price) this.response = "Insufficient credits! You need " + price + " credits to unlock this Augment!";
            else if(active.hasAugment(augment)) this.response = active.getName() + " already has that Augment equipped!";
            else if(PokemonAugmentRegistry.isIncompatibleWith(augment, active.getAugments())) this.response = "That Augment is incompatible with at least one of the equipped Augments!";
            else if(!active.isValidAugment(augment)) this.response = active.getName() + " cannot equip that Augment!";
            else if(active.getAvailableAugmentSlots() < augment.getSlotCost()) this.response = "There are not enough slots available to equip that Augment! " + active.getName() + " has " + active.getAvailableAugmentSlots() + " available, and that Augment needs " + augment.getSlotCost() + " slots!";
            else
            {
                String credits = "";
                if(!owned)
                {
                    this.playerData.changeCredits(price * -1);
                    this.playerData.addAugment(augment.getAugmentID());
                    credits = "Purchased the " + augment.getAugmentName() + " augment for " + price + " credits! ";
                }

                active.equipAugment(augment);
                active.updateAugments();
                this.response = credits + "\nSuccessfully equipped the " + augment.getAugmentName() + " augment onto " + active.getName() + "!";
            }
        }
        else if(remove)
        {
            Pokemon active = this.playerData.getSelectedPokemon();

            List<PokemonAugment> equipped = new ArrayList<>(active.getAugments());
            int index = this.getInt(2) - 1;

            if(DuelHelper.isInDuel(this.player.getId())) this.response = "You cannot change Augments while in a Duel!";
            else if(equipped.isEmpty())
            {
                this.response = active.getName() + " does not have any Augments equipped!";
                return this;
            }
            else if(index < 0 || index >= equipped.size())
            {
                this.response = "Invalid number!";
                return this;
            }
            else
            {
                PokemonAugment augment = equipped.get(index);

                if(!active.hasAugment(augment)) this.response = active.getName() + " does not have that Augment equipped!";
                else
                {
                    active.removeAugment(augment);
                    active.updateAugments();
                    this.response = "Successfully removed the " + augment.getAugmentName() + " augment from " + active.getName() + "!";
                }
            }
        }
        else if(clear)
        {
            Pokemon active = this.playerData.getSelectedPokemon();

            if(DuelHelper.isInDuel(this.player.getId())) this.response = "You cannot change Augments while in a Duel!";
            else if(active.getAugments().isEmpty()) this.response = active.getName() + " does not have any Augments equipped!";
            else
            {
                active.resetAugments();
                this.response = "Successfully cleared all Augments from %s!".formatted(active.getName());
            }
        }
        else
        {
            Pokemon active = this.playerData.getSelectedPokemon();

            int totalSlots = active.getTotalAugmentSlots();
            int availableSlots = active.getAvailableAugmentSlots();
            int usedSlots = totalSlots - active.getAvailableAugmentSlots();
            String nextSlot = active.getLevelForNextSlot() == -1 ? "" : "*Next slot unlocked at Level " + active.getLevelForNextSlot() + ".*";
            this.embed.setDescription("Slot Usage: " + usedSlots + " / " + totalSlots + "\n" + ":red_square:".repeat(usedSlots) + ":green_square:".repeat(availableSlots) + "\n" + nextSlot);

            StringBuilder equippedAugments = new StringBuilder();

            List<PokemonAugment> activeAugments = new ArrayList<>(active.getAugments());
            if(activeAugments.isEmpty()) equippedAugments.append("No augments equipped! Use `p!augments info` to learn more about Augments.");
            else for(int i = 0; i < activeAugments.size(); i++)
            {
                PokemonAugment augment = activeAugments.get(i);

                String number = (i + 1) + ": ";
                String data = String.join(" | ", augment.getAugmentName(), "Slots: " + augment.getSlotCost());
                equippedAugments.append(number).append(data).append("\n");
            }

            equippedAugments.append("\n\nRemove augments using `p!augments remove <ID>`! Equip augments using `p!augments equip <ID>`!");
            this.embed.addField("Augment Overview", equippedAugments.toString(), false);
            
            List<String> availableAugments = new ArrayList<>();

            PokemonAugmentRegistry.PokemonAugmentData augmentData = PokemonAugmentRegistry.AUGMENT_DATA.get(active.getName());
            List<PokemonAugment> availableList = augmentData.getOrderedAugmentList();

            for(int i = 0; i < availableList.size(); i++)
            {
                PokemonAugment augment = availableList.get(i);
                int level = augmentData.getAugmentsInfo().entrySet().stream().filter(e -> e.getValue().contains(augment)).findFirst().get().getKey();

                String tag;
                if(active.hasAugment(augment) || PokemonAugmentRegistry.isIncompatibleWith(augment, active.getAugments())) tag = ":no_entry_sign:";
                else if(augment.getSlotCost() > active.getAvailableAugmentSlots() && level <= active.getLevel()) tag = ":yellow_circle:";
                else if(level <= active.getLevel()) tag = ":green_circle:";
                else tag = ":red_circle:";

                String number = (i + 1) + ": ";
                String data = String.join(" | ", augment.getAugmentName() + " " + tag, "Slots: " + augment.getSlotCost(), "Level: " + level);
                availableAugments.add(number + data);
            }

            this.embed.addField("Available Augments", String.join("\n", availableAugments), false);

            this.embed.setTitle(active.getName() + " Augments");
        }

        return this;
    }
}
