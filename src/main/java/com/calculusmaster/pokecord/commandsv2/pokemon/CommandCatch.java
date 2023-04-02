package com.calculusmaster.pokecord.commandsv2.pokemon;

import com.calculusmaster.pokecord.commandsv2.CommandData;
import com.calculusmaster.pokecord.commandsv2.CommandV2;
import com.calculusmaster.pokecord.game.bounties.objectives.CatchNameObjective;
import com.calculusmaster.pokecord.game.bounties.objectives.CatchPoolObjective;
import com.calculusmaster.pokecord.game.bounties.objectives.CatchTypeObjective;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.player.Settings;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.mongo.CollectionsQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import com.calculusmaster.pokecord.util.helpers.ThreadPoolHandler;
import com.calculusmaster.pokecord.util.helpers.event.SpawnEventHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CommandCatch extends CommandV2
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

                int maxLevel = 15 + switch(caught.getRarity()) {
                    case COPPER, SILVER, GOLD -> 0;
                    case DIAMOND, PLATINUM -> -2;
                    case MYTHICAL, ULTRA_BEAST -> -3;
                    case LEGENDARY -> -5;
                } + 1;

                caught.setLevel(random.nextInt(baseLevel, maxLevel));

                //Database Stuff
                ThreadPoolHandler.CATCH.execute(() -> {
                    caught.upload();
                    this.playerData.addPokemon(caught.getUUID());

                    if(this.playerData.getSettings().get(Settings.CLIENT_CATCH_AUTO_INFO, Boolean.class));
                    //TODO: Fix this with new Commands System
                        //com.calculusmaster.pokecord.commands.Commands.execute("info", this.event, new String[]{"info", "latest"});

                    //Achievements TODO - Restructure Achievements
                    Achievements.CAUGHT_FIRST_POKEMON.grant(this.player.getId(), event.getChannel().asTextChannel());

                    //Statistics TODO - Restructure Statistics to be more like telemetry, with a lot more data collected
                    this.playerData.getStatistics().incr(PlayerStatistic.POKEMON_CAUGHT);

                    //Bounties
                    this.playerData.updateBountyProgression(b -> {
                        switch(b.getType()) {
                            case CATCH_POKEMON -> b.update();
                            case CATCH_POKEMON_TYPE -> b.updateIf(caught.isType(((CatchTypeObjective)b.getObjective()).getType()));
                            case CATCH_POKEMON_NAME -> b.updateIf(caught.getEntity().toString().equals(((CatchNameObjective)b.getObjective()).getName()));
                            case CATCH_POKEMON_POOL -> b.updateIf(((CatchPoolObjective)b.getObjective()).getPool().contains(caught.getEntity().toString()));
                        }
                    });
                });

                //Collections TODO - Redo collections system
                CollectionsQuery collection = new CollectionsQuery(caught.getName(), this.player.getId());

                collection.increase();
                int amount = collection.getCaughtAmount();
                int[] rarityCredits = this.collectionCredits(caught.getRarity());

                if(amount == -1 || amount == 0) this.response = "An error has occurred with collections!";
                else if(amount % 5 == 0)
                {
                    int credits = rarityCredits[1] + rarityCredits[2] * (amount / 5 - 1);
                    this.playerData.changeCredits(credits);

                    extraResponses.add("Reached Collection Milestone for " + caught.getName() + ": **" + amount + "** (**+" + credits + "**c)!");
                }
                else if(amount == 1)
                {
                    int credits = rarityCredits[0];
                    this.playerData.changeCredits(credits);

                    extraResponses.add("Unlocked Collection for " + caught.getName() + " (**+" + credits + "**c)!");
                }

                if(amount >= 10) Achievements.REACHED_COLLECTION_MILESTONE_10.grant(this.player.getId(), event.getChannel().asTextChannel());
                if(amount >= 20) Achievements.REACHED_COLLECTION_MILESTONE_20.grant(this.player.getId(), event.getChannel().asTextChannel());
                if(amount >= 50) Achievements.REACHED_COLLECTION_MILESTONE_50.grant(this.player.getId(), event.getChannel().asTextChannel());


                //Redeem Unlocks on High IV Catches
                if(caught.getTotalIVRounded() >= 90 || (caught.getTotalIVRounded() >= 80 && new Random().nextInt(100) < 20))
                {
                    this.playerData.changeRedeems(1);

                    this.playerData.getStatistics().incr(PlayerStatistic.NATURAL_REDEEMS_EARNED);

                    extraResponses.add("You earned a redeem for catching a Pokemon with high IVs!");
                }

                //Main Response
                this.response = "You caught a **Level " + caught.getLevel() + " " + caught.getName() + "**!";

                if(!extraResponses.isEmpty())
                {
                    extraResponses.add(0, event.getMember().getAsMention());
                    event.getChannel().sendMessage(String.join("\n", extraResponses)).queue();
                }

                SpawnEventHelper.clearSpawn(this.server.getId());

                return true;
            }
        }
    }

    private int[] collectionCredits(PokemonRarity.Rarity r)
    {
        return switch(r) {
            //Format: new int[] {<new collection>, <base for milestone>, <multiplier per milestone level>}
            case COPPER -> new int[]{150, 200, 150};
            case SILVER -> new int[]{175, 215, 165};
            case GOLD -> new int[]{200, 240, 175};
            case DIAMOND -> new int[]{225, 260, 190};
            case PLATINUM -> new int[]{250, 300, 200};
            case MYTHICAL -> new int[]{300, 350, 250};
            case ULTRA_BEAST -> new int[]{325, 375, 275};
            case LEGENDARY -> new int[]{500, 400, 750};
        };
    }
}
