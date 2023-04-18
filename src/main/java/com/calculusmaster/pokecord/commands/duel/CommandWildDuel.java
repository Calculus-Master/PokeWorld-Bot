package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.component.DuelFlag;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.extension.WildDuel;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class CommandWildDuel extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("wild-duel")
                .withConstructor(CommandWildDuel::new)
                .withFeature(Feature.PVE_DUELS)
                .withCommand(Commands
                        .slash("wild-duel", "Duel wild Pokemon!")
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        if(DuelHelper.isInDuel(this.player.getId())) return this.error("You are already in a Duel.");

        PokemonEntity opponent = PokemonRarity.getPokemon();
        Duel duel = WildDuel.create(this.player.getId(), event.getChannel().asTextChannel(), opponent);
        duel.addFlags(DuelFlag.SWAP_BANNED, DuelFlag.ZMOVES_BANNED, DuelFlag.DYNAMAX_BANNED);

        event.reply("A wild **" + opponent.getName() + "** appeared to challenge you!").queue();
        this.setResponsesHandled();

        duel.sendTurnEmbed();

        return true;
    }
}
