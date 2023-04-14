package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.duel.trainer.TrainerManager;
import com.calculusmaster.pokecord.game.objectives.Bounty;
import com.calculusmaster.pokecord.game.player.PlayerBounties;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugment;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.mongo.DatabaseCollection;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.cacheold.PlayerDataCache;
import com.calculusmaster.pokecord.util.helpers.event.SpawnEventHelper;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CommandDev extends PokeWorldCommand
{
    public static List<String> DEVELOPERS = List.of("309135641453527040", "1065845045665738852");

    public static void init()
    {
        CommandData
                .create("dev")
                .withConstructor(CommandDev::new)
                .withCommand(Commands
                        .slash("dev", "Secret developer powers.")
                        .addOption(OptionType.STRING, "command", "Developer command to run.", true, false)
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        if(!DEVELOPERS.contains(event.getUser().getId())) return this.error("You are not a developer.");

        OptionMapping commandOption = Objects.requireNonNull(event.getOption("command"));

        String[] command = commandOption.getAsString().split("-");

        //Return all the current active Spawn Event Helper timers, the server they're for, and their delay
        switch(command[0])
        {
            case "getspawntimers" ->
            {
                List<String> spawnTimers = SpawnEventHelper.getSnapshot();
                this.response = "SpawnEventHelper Snapshot:\n\n" + String.join("\n - ", spawnTimers);
            }
            case "addpokemon" ->
            {
                PokemonEntity e = command.length > 1 ? PokemonEntity.cast(command[1]) : PokemonEntity.getRandom();
                Pokemon p = Pokemon.create(e);
                p.upload();
                this.playerData.addPokemon(p.getUUID());
                this.response = "Added a new **" + p.getName() + "** to your Pokemon.";
            }
            case "reset" ->
            {
                PlayerDataQuery target = command.length > 1 ? PlayerDataQuery.of(command[1]) : this.playerData;
                if(!target.getOwnedEggIDs().isEmpty())
                    target.getOwnedEggs().forEach(e -> Mongo.deleteOne("CommandDev - Reset Player (Delete Eggs)", DatabaseCollection.EGG, Filters.eq("eggID", e.getEggID())));
                if(!target.getPokemonList().isEmpty()) target.getPokemon().forEach(Pokemon::delete);
                Mongo.MarketData.deleteMany(Filters.eq("sellerID", target.getID()));
                Mongo.deleteOne("CommandDev - Reset Player (Delete Settings)", DatabaseCollection.SETTINGS, Filters.eq("playerID", target.getID()));
                Mongo.deleteOne("CommandDev - Reset Player (Delete Statistics)", DatabaseCollection.STATISTICS, Filters.eq("playerID", target.getID()));
                Mongo.deleteOne("CommandDev - Reset Player (Delete Player Data)", DatabaseCollection.PLAYER, Filters.eq("playerID", target.getID()));
                PlayerDataCache.CACHE.remove(target.getID());
                this.response = target.getUsername() + " has been reset!";
            }
            case "resettrainers" ->
            {
                Mongo.TrainerData.deleteMany(Filters.exists("trainerID"));
                TrainerManager.createRegularTrainers();
                this.response = "Trainers have been deleted and re-created.";
            }
            case "clearcrashreports" ->
            {
                Mongo.CrashData.deleteMany(Filters.exists("error"));
                this.response = "Crash Reports have been deleted.";
            }
            case "addcredits" ->
            {
                int amount = command.length > 1 ? Integer.parseInt(command[1]) : 1000;
                this.playerData.changeCredits(amount);
                this.response = "Added " + amount + " credits.";
            }
            case "addaugment" ->
            {
                PokemonAugment a = PokemonAugment.valueOf(command[1]);
                this.playerData.getInventory().addAugment(a);
                this.response = "Added " + a.getAugmentName() + " to your inventory.";
            }
            case "addbounty" ->
            {
                PlayerBounties bounties = this.playerData.getBounties();

                if(bounties.getBounties().size() == PlayerBounties.MAX_BOUNTIES) this.response = "Maximum bounties reached.";
                else
                {
                    bounties.add(Bounty.create());
                    this.response = "Added new bounty.";
                }
            }
            default -> {
                return this.error("Invalid dev command: " + Arrays.toString(command));
            }
        }

        return true;
    }
}
