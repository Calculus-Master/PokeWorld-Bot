package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.bounties.objectives.ReleaseNameObjective;
import com.calculusmaster.pokecord.game.bounties.objectives.ReleasePoolObjective;
import com.calculusmaster.pokecord.game.bounties.objectives.ReleaseTypeObjective;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CommandRelease extends PokeWorldCommand
{
    private static final Map<String, Integer> RELEASE_REQUESTS = new HashMap<>();

    public static void init()
    {
        CommandData
                .create("release")
                .withConstructor(CommandRelease::new)
                .withCommand(Commands
                        .slash("release", "Release a Pokemon you no longer want into the wild!")
                        .addSubcommands(
                                new SubcommandData("pokemon", "Release a specific Pokemon you longer want.")
                                        .addOption(OptionType.INTEGER, "number", "The number of the Pokemon you want to release.", true),
                                new SubcommandData("confirm", "Confirm the release of a Pokemon you no longer want."),
                                new SubcommandData("cancel", "Cancel the release of a Pokemon you requested to release.")
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        if(subcommand.equals("pokemon"))
        {
            OptionMapping numberOption = Objects.requireNonNull(event.getOption("number"));
            int num = numberOption.getAsInt();

            List<String> pokemonList = this.playerData.getPokemonList();

            if(pokemonList.size() == 1) return this.error("You cannot release your only Pokemon.");
            else if(num < 1 || num > pokemonList.size()) return this.error("Invalid Pokemon number.");
            else
            {
                Pokemon p = Pokemon.build(pokemonList.get(num - 1));

                RELEASE_REQUESTS.put(this.player.getId(), num);
                this.response = "Are you sure you want to release your **Level " + p.getLevel() + " " + p.getName() + (p.hasNickname() ? " (Nicknamed: " + p.getDisplayName() + ")" : "") + "** into the wild? ***This action cannot be undone!***\n*Use* `/release confirm` *to confirm, or* `/release cancel` *to cancel.*";
            }
        }
        else if(subcommand.equals("confirm"))
        {
            if(!RELEASE_REQUESTS.containsKey(this.player.getId())) return this.error("You do not have an active release request. Use `/release pokemon` to release one of your Pokemon.");

            String UUID = this.playerData.getPokemonList().get(RELEASE_REQUESTS.get(this.player.getId()) - 1);
            Pokemon p = Pokemon.build(UUID);

            this.playerData.updateBountyProgression(b -> {
                switch(b.getType()) {
                    case RELEASE_POKEMON -> b.update();
                    case RELEASE_POKEMON_TYPE -> {
                        if(p.isType(((ReleaseTypeObjective)b.getObjective()).getType())) b.update();
                    }
                    case RELEASE_POKEMON_NAME -> {
                        if(p.getEntity().toString().equals(((ReleaseNameObjective)b.getObjective()).getName())) b.update();
                    }
                    case RELEASE_POKEMON_POOL -> {
                        if(((ReleasePoolObjective)b.getObjective()).getPool().contains(p.getEntity().toString())) b.update();
                    }
                }
            });

            this.playerData.removePokemon(UUID);
            p.delete();

            RELEASE_REQUESTS.remove(this.player.getId());

            this.response = "You released your **Level " + p.getLevel() + " " + p.getName() + (p.hasNickname() ? " (Nicknamed: " + p.getDisplayName() + ")" : "") + "** into the wild!";
        }
        else if(subcommand.equals("cancel"))
        {
            if(!RELEASE_REQUESTS.containsKey(this.player.getId())) return this.error("You do not have an active release request. Use `/release pokemon` to release one of your Pokemon.");

            RELEASE_REQUESTS.remove(this.player.getId());
            this.response = "You successfully cancelled your release request!";
        }

        return true;
    }
}
