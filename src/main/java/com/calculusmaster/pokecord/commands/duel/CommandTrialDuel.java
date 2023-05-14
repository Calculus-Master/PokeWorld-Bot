package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.component.DuelFlag;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.extension.ZTrialDuel;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.util.enums.Prices;
import com.calculusmaster.pokecord.util.enums.StatisticType;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Arrays;
import java.util.Objects;

public class CommandTrialDuel extends PokeWorldCommand
{
    public static final int MOVE_REQUIREMENT = 400;

    public static void init()
    {
        CommandData
                .create("trial-duel")
                .withConstructor(CommandTrialDuel::new)
                .withFeature(Feature.PVE_DUELS_ZTRIAL)
                .withCommand(Commands
                        .slash("trial-duel", "Complete a Trial duel to earn typed Z-Crystals!")
                        .addOption(OptionType.STRING, "type", "The Trial Pokemon type you want to challenge. This will dictate the Z-Crystal reward.", true, true)
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        if(DuelHelper.isInDuel(this.player.getId())) return this.error("You are already in a Duel.");

        OptionMapping typeOption = Objects.requireNonNull(event.getOption("type"));
        String typeInput = typeOption.getAsString();

        Type type = Type.cast(typeInput);
        if(type == null) return this.error("\"" + typeInput + "\" is not a valid Pokemon Type!");

        ZCrystal z = Objects.requireNonNull(ZCrystal.getCrystalOfType(type));

        if(this.playerData.getInventory().hasZCrystal(z))
            return this.error("You have already acquired **" + z.getName() + "**. You can only acquire each Z-Crystal once!");
        else if(!this.playerData.getSelectedPokemon().isType(type))
            return this.error("You can only challenge the **" + type.getStyledName() + "**-Type Trial with a **" + type.getStyledName() + "**-Type Pokemon!");
        else if(this.playerData.getCredits() < Prices.TRIAL_DUEL.get())
            return this.error("Insufficient credits! You need **" + Prices.TRIAL_DUEL.get() + "** Credits to challenge a Trial Duel!");
        else if(this.playerData.getStatistics().get(StatisticType.MOVES_USED) < MOVE_REQUIREMENT)
            return this.error("You need to use **" + (MOVE_REQUIREMENT - this.playerData.getStatistics().get(StatisticType.MOVES_USED)) + "** more moves to be eligible to challenge a Trial Duel!");

        this.playerData.changeCredits(-Prices.TRIAL_DUEL.get());

        Duel duel = ZTrialDuel.create(this.player.getId(), event.getChannel().asTextChannel(), type);
        duel.addFlags(DuelFlag.SWAP_BANNED, DuelFlag.ZMOVES_BANNED, DuelFlag.DYNAMAX_BANNED);

        event.reply("A **" + type.getStyledName() + "**-Type Trial Pokemon challenges you!").queue();
        this.setResponsesHandled();

        duel.sendTurnEmbed();

        return true;
    }

    @Override
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event)
    {
        if(event.getFocusedOption().getName().equals("type"))
            event.replyChoiceStrings(this.getAutocompleteOptions(event.getFocusedOption().getValue(), Arrays.stream(Type.values()).map(Type::getStyledName).toList())).queue();

        return true;
    }
}
