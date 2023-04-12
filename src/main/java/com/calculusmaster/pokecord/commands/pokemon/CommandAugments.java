package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugment;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugmentRegistry;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.*;
import java.util.stream.Collectors;

public class CommandAugments extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("augments")
                .withConstructor(CommandAugments::new)
                .withFeature(Feature.AUGMENT_POKEMON)
                .withCommand(Commands
                        .slash("augments", "Enhance Pokemon with augments to boost their power in Duels!")
                        .addSubcommands(
                                new SubcommandData("view", "View your active Pokemon's augments, and all their available augments."),
                                new SubcommandData("guide", "Mini-Tutorial: Learn about augments and how to use them."),
                                new SubcommandData("info", "View information about a specific augment.")
                                        .addOption(OptionType.STRING, "name", "Name of the augment to view.", true, true),
                                new SubcommandData("equip", "Equip an augment to your active Pokemon.")
                                        .addOption(OptionType.STRING, "name", "Name of the augment to equip.", true, true),
                                new SubcommandData("remove", "Remove an augment from your active Pokemon.")
                                        .addOption(OptionType.STRING, "name", "Name of the augment to remove.", true, true),
                                new SubcommandData("clear", "Remove all augments from your active Pokemon.")
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        if(subcommand.equals("view"))
        {
            Pokemon active = this.playerData.getSelectedPokemon();

            int totalSlots = active.getTotalAugmentSlots();
            int availableSlots = active.getAvailableAugmentSlots();
            int usedSlots = totalSlots - availableSlots;

            this.embed
                    .setTitle(active.getName() + "'s Augments")
                    .setDescription("""
                    Slots Used: `%d / %d`
                    %s
                    *%s*
                    """.formatted(usedSlots, totalSlots,
                    ":red_square:".repeat(usedSlots) + ":green_square:".repeat(availableSlots),
                    active.getLevelForNextSlot() == -1 ? "All augment slots unlocked." : (totalSlots == 0 ? "First" : "Next") + " augment slot unlocks at __Level " + active.getLevelForNextSlot() + "__."
            ));

            //Equipped
            EnumSet<PokemonAugment> equipped = active.getAugments();

            if(equipped.isEmpty()) this.embed.addField("Equipped", "*None.*", false);
            else
            {
                List<String> names = new ArrayList<>(), slotCosts = new ArrayList<>();

                for(PokemonAugment a : equipped)
                {
                    names.add("**" + a.getAugmentName() + "**");
                    slotCosts.add(String.valueOf(a.getSlotCost()));
                }

                this.embed
                        .addField("Equipped Augments", String.join("\n", names), true)
                        .addField("Slot Cost", String.join("\n", slotCosts), true)
                        .addBlankField(true);
            }

            //Available
            PokemonAugmentRegistry.PokemonAugmentData augmentData = PokemonAugmentRegistry.AUGMENT_DATA.get(active.getEntity());
            List<PokemonAugment> available = augmentData.getOrderedAugmentList();

            List<String> names = new ArrayList<>(), slotCosts = new ArrayList<>(), level = new ArrayList<>();

            for(PokemonAugment a : available)
            {
                int augmentLevel = augmentData.getAugmentsInfo().entrySet().stream().filter(e -> e.getValue().contains(a)).map(Map.Entry::getKey).findFirst().orElse(-1);

                String emote;
                if(active.getLevel() < augmentLevel || !this.playerData.getInventory().getOwnedAugments().contains(a)) emote = ":lock:";
                else if(active.hasAugment(a)) emote = ":black_circle:";
                else if(PokemonAugmentRegistry.isIncompatibleWith(a, active.getAugments())) emote = ":red_circle:";
                else if(availableSlots < a.getSlotCost()) emote = ":yellow_circle:";
                else emote = ":green_circle:";

                names.add(emote + " **" + a.getAugmentName() + "**");
                slotCosts.add(String.valueOf(a.getSlotCost()));
                level.add(augmentLevel == -1 ? "ERROR" : String.valueOf(augmentLevel));
            }

            this.embed
                    .addField("Available Augments", String.join("\n", names), true)
                    .addField("Slot Cost", String.join("\n", slotCosts), true)
                    .addField("Level", String.join("\n", level), true)
                    .setFooter("For more information about the augments system, use /augments guide!");
        }
        else if(subcommand.equals("guide"))
        {
            this.embed
                    .setTitle("Pokemon Augments: A \"Short\" Guide")
                    .setDescription("""
                            Augments are modifications that can alter various characteristics of your Pokemon. Augment effects range from simply boosting a Pokemon's stats, to greatly modifying how one of their moves works in a battle.
                            The system may feel complicated, so here are explanations on the main components of the system!
                            """)
                    .addField("Augment Slots", """
                            Every augment has an associated "slot cost". Similarly, every Pokemon has a certain number of "augment slots".
                            The number of augment slots a Pokemon has determines how many of their available augments they can equip. If you equip some augments, you may be unable to equip augments with too high of a slot cost!
                            
                            Pokemon will unlock more augment slots as they level up, and the maximum number of slots a Pokemon can unlock is determined by its rarity.
                            Use `/augments view` to get a general idea of your Pokemon's augment slots, and the associated costs for each of their available augments.
                            """, false)
                    .addField("Unlocking Augments", """
                            Augments have various sources. You must acquire an augment from its source before you can equip it to your Pokemon.
                            Once you've acquired an augment, its yours forever! You can equip it to as many Pokemon (provided they're able to) as you want.
                            You can use `/augments info` to see the source of an augment (and some other helpful information).
                            
                            Quick note: Every Pokemon can only equip certain augments – you can see all of a Pokemon's available augments by using `/augments view`.
                            
                            Upon reaching the Pokemon Mastery Level required to unlock the augments system, you have received a couple of free augments.
                            These are the basic stat boost augments – to get you started with augmenting your Pokemon!
                            """, false)
                    .addField("Augment-Related Commands", """
                            `/augments equip` – Equip an augment to your active Pokemon.
                            `/augments remove` – Remove an equipped augment from your active Pokemon.
                            `/augments clear` – Remove all equipped augments from your active Pokemon.
                            `/augments view` – View your active Pokemon's augment slots and equipped augments.
                            `/augments info` – View information about a specific augment.
                            """, false);
        }
        else if(subcommand.equals("info"))
        {
            OptionMapping nameOption = Objects.requireNonNull(event.getOption("name"));
            String nameInput = nameOption.getAsString();

            PokemonAugment augment = PokemonAugment.cast(nameInput);
            if(augment == null) return this.error("\"" + nameInput + "\" is not a valid augment name!");

            List<String> augmentPokemon = PokemonAugmentRegistry.AUGMENT_DATA.entrySet().stream().filter(e -> e.getValue().has(augment)).map(e -> e.getKey().getName()).collect(Collectors.toList());
            Collections.shuffle(augmentPokemon);
            String augmentPokemonContents = augmentPokemon.stream().limit(15).map(s -> "- " + s).collect(Collectors.joining("\n"));

            this.embed
                    .setTitle("Augment Info: " + augment.getAugmentName())
                    .setDescription("""
                            *%s*
                            
                            %s
                            """.formatted(augment.getAugmentDescription(), this.playerData.getInventory().getOwnedAugments().contains(augment) ? "You own this augment!" : "You have not acquired this augment."))
                    .addField("Source", augment.getSource().isEmpty() ? "*Not available.*" : augment.getSource(), false)
                    .addField("Slot Cost", "`" + augment.getSlotCost() + "`", true)
                    .addBlankField(true)
                    .addBlankField(true)
                    .addField("Pokemon", "A total of " + augmentPokemon.size() + " Pokemon can equip this augment.\nHere are some:\n" + augmentPokemonContents, false)
                    .setFooter("Augment ID: " + augment.getAugmentID());
        }
        else if(subcommand.equals("equip") || subcommand.equals("remove"))
        {
            boolean equip = subcommand.equals("equip");
            OptionMapping nameOption = Objects.requireNonNull(event.getOption("name"));
            String nameInput = nameOption.getAsString();

            PokemonAugment augment = PokemonAugment.cast(nameInput);
            if(augment == null) return this.error("\"" + nameInput + "\" is not a valid augment name!");
            else if(DuelHelper.isInDuel(this.player.getId())) return this.error("You cannot " + subcommand + " augments while in a Duel!");

            Pokemon active = this.playerData.getSelectedPokemon();

            if(equip)
            {
                List<PokemonAugment> available = PokemonAugmentRegistry.AUGMENT_DATA.get(active.getEntity()).getOrderedAugmentList();

                if(!available.contains(augment)) return this.error(active.getName() + " cannot equip the augment *" + augment.getAugmentName() + "*.");
                else if(!this.playerData.getInventory().getOwnedAugments().contains(augment)) return this.error("You have not unlocked the augment *" + augment.getAugmentName() + "*. Use `/augment info` to view how to unlock the augment!");
                else if(active.getAugments().contains(augment)) return this.error(active.getName() + " already has that augment equipped.");
                else if(PokemonAugmentRegistry.isIncompatibleWith(augment, active.getAugments())) return this.error("*" + augment.getAugmentName() + "* is incompatible with one or more of " + active.getName() + "'s equipped augments.");
                else if(active.getAvailableAugmentSlots() < augment.getSlotCost()) return this.error(active.getName() + " does not have enough augment slots available to equip the augment *" + augment.getAugmentName() + "*. Required Slots: " + augment.getSlotCost() + ", Available Slots: " + active.getAvailableAugmentSlots() + ". Remove an augment using `/augment remove` to free up more available slots, or equip an augment with lower cost!");

                active.equipAugment(augment);
                active.updateAugments();

                this.response = "The augment **" + augment.getAugmentName() + "** has been equipped to " + active.getName() + "!";
            }
            else
            {
                if(!active.getAugments().contains(augment)) return this.error(active.getName() + " does not have the augment *" + augment.getAugmentName() + "* equipped.");

                active.removeAugment(augment);
                active.updateAugments();

                this.response = "The augment **" + augment.getAugmentName() + "** has been removed from " + active.getName() + "!";
            }
        }
        else if(subcommand.equals("clear"))
        {
            if(DuelHelper.isInDuel(this.player.getId())) return this.error("You cannot edit augments while in a Duel!");

            Pokemon active = this.playerData.getSelectedPokemon();

            if(active.getAugments().isEmpty()) return this.error(active.getName() + " does not have any augments equipped.");

            active.clearAugments();
            active.updateAugments();

            this.response = "All augments have been removed from " + active.getName() + "!";
        }

        return true;
    }

    @Override
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event)
    {
        String currentInput = event.getFocusedOption().getValue();

        if(event.getFocusedOption().getName().equals("name"))
            event.replyChoiceStrings(this.getAutocompleteOptions(currentInput, Arrays.stream(PokemonAugment.values()).map(PokemonAugment::getAugmentName).toList())).queue();

        return true;
    }
}
