package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.extension.EliteDuel;
import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestrictionRegistry;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.player.PlayerTeam;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class CommandEliteDuel extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("elite-duel")
                .withConstructor(CommandEliteDuel::new)
                .withFeature(Feature.PVE_DUELS_ELITE)
                .withCommand(Commands
                        .slash("elite-duel", "Duel randomly generated powerful Elite Trainers for large rewards!")
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        if(DuelHelper.isInDuel(this.player.getId())) return this.error("You are already in a Duel.");

        PlayerTeam team = this.playerData.getTeam();

        if(team.size() < 6) return this.error("You must have 6 Pokemon in your team to challenge an Elite Trainer.");
        else if(!TeamRestrictionRegistry.STANDARD.validate(team.getActiveTeamPokemon())) return this.error("Your team does not meet the standard restrictions.");

        Duel duel = EliteDuel.create(this.player.getId(), event.getChannel().asTextChannel());

        event.reply("You've challenged an Elite Trainer!").queue();
        this.setResponsesHandled();

        duel.sendTurnEmbed();

        return true;
    }
}
