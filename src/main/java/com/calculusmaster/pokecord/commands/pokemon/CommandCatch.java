package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.functional.Achievement;
import com.calculusmaster.pokecord.game.objectives.ObjectiveType;
import com.calculusmaster.pokecord.game.player.PlayerPokedex;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.evolution.FormRegistry;
import com.calculusmaster.pokecord.util.enums.StatisticType;
import com.calculusmaster.pokecord.util.helpers.ThreadPoolHandler;
import com.calculusmaster.pokecord.util.helpers.event.SpawnEventHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.*;

public class CommandCatch extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("catch")
                .withConstructor(CommandCatch::new)
                .withFeature(Feature.CATCH_POKEMON)
                .withCommand(Commands
                        .slash("catch", "Try to catch Pokemon that spawn!")
                        .addOption(OptionType.STRING, "name", "Your guess for the name of the Pokemon that spawned. If correct, it'll be yours!", true)
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        OptionMapping guessOption = Objects.requireNonNull(Objects.requireNonNull(event.getOption("name")));

        String guess = guessOption.getAsString();
        SpawnEventHelper.SpawnData spawnData = SpawnEventHelper.getSpawn(this.server.getId());

        if(spawnData == null) return this.error("Nothing has spawned in this server.");
        else
        {
            boolean correct = guess.equalsIgnoreCase(spawnData.getSpawn().getName()) ||
                    guess.equalsIgnoreCase(spawnData.getSpawn().toString().replaceAll("_", " "));

            if(!correct) return this.error("Incorrect guess!");
            else
            {
                final Random random = new Random();
                final List<String> extraResponses = new ArrayList<>();

                //Create caught Pokemon
                Pokemon caught = Pokemon.create(spawnData.getSpawn());

                //Data from the SpawnEventHelper
                caught.setShiny(spawnData.isShiny());
                caught.setCustomData(spawnData.getCustomData());

                //Set random Level
                int baseLevel = switch(this.playerData.getLevel()) {
                    case 7, 8 -> 3;
                    case 9, 10, 11 -> 5;
                    case 12 -> 8;
                    default -> 1;
                };

                caught.setLevel(random.nextInt(baseLevel, baseLevel + 5 + 1));

                //PokeDex

                PlayerPokedex pokedex = this.playerData.getPokedex();
                int a = pokedex.add(caught.getEntity());

                int credits = pokedex.getCollectionReward(caught.getEntity());
                String creditsText = credits == 0 ? "" : "(**+" + credits + "c**)";
                if(a == 1) extraResponses.add("*Added " + caught.getName() + " to your PokeDex.* " + creditsText);
                else if(a % 5 == 0) extraResponses.add("*Reached new PokeDex Milestone for " + caught.getName() + ": **" + a + "** " + creditsText);

                if(credits > 0) this.playerData.changeCredits(credits);

                //Database Stuff
                ThreadPoolHandler.CATCH.execute(() -> {
                    caught.upload();
                    this.playerData.addPokemon(caught.getUUID());

                    //Statistics
                    this.playerData.getStatistics().increase(StatisticType.POKEMON_CAUGHT);

                    //Bounties
                    this.playerData.updateObjective(ObjectiveType.CATCH_POKEMON, 1);
                    this.playerData.updateObjective(ObjectiveType.CATCH_POKEMON_TYPE, o -> caught.isType(o.asType().getType()), 1);
                    this.playerData.updateObjective(ObjectiveType.CATCH_POKEMON_NAME, o -> caught.is(o.asPokemon().getEntity()), 1);
                    this.playerData.updateObjective(ObjectiveType.CATCH_POKEMON_POOL, o -> o.asPokemonList().getList().contains(caught.getEntity()), 1);

                    //Achievements
                    Achievement.COMPLETE_POKEDEX.grant(this.playerData, () -> Arrays.stream(PokemonEntity.values()).allMatch(pokedex::hasCollected), this.channel);

                    //Update PokeDex
                    this.playerData.updatePokedex();
                });

                //Form Checks
                if(FormRegistry.hasFormData(caught.getEntity()))
                {
                    if(!this.playerData.getOwnedForms().contains(caught.getEntity()))
                        this.playerData.addOwnedForm(caught.getEntity());
                }

                //Main Response
                this.response = "You caught a **Level " + caught.getLevel() + " " + caught.getName() + "**!"
                        + (!extraResponses.isEmpty() ? "\n" + String.join("\n", extraResponses) : "");

                SpawnEventHelper.clearSpawn(this.server.getId());

                return true;
            }
        }
    }
}
