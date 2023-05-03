package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.EggGroup;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.player.level.MasteryLevelManager;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.world.PokeWorldNursery;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static com.calculusmaster.pokecord.game.enums.elements.EggGroup.DITTO;
import static com.calculusmaster.pokecord.game.enums.elements.EggGroup.NO_EGGS;
import static com.calculusmaster.pokecord.game.enums.elements.Gender.UNKNOWN;

public class CommandBreed extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("breed")
                .withConstructor(CommandBreed::new)
                .withFeature(Feature.BREED_POKEMON)
                .withCommand(Commands
                        .slash("breed", "Breed Pokemon to create stronger Pokemon Eggs!")
                        .addSubcommands(
                                new SubcommandData("pokemon", "Breed two of your owned Pokemon.")
                                        .addOption(OptionType.INTEGER, "number1", "The number of the first Pokemon.", true)
                                        .addOption(OptionType.INTEGER, "number2", "The number of the second Pokemon.", true),
                                new SubcommandData("nursery", "View your Pokemon that are in the " + Pokeworld.NAME + " Nursery.")
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
            OptionMapping number1Option = Objects.requireNonNull(event.getOption("number1"));
            OptionMapping number2Option = Objects.requireNonNull(event.getOption("number2"));

            int num1 = number1Option.getAsInt();
            int num2 = number2Option.getAsInt();

            if(num1 < 0 || num2 < 0 || num1 > this.playerData.getPokemonList().size() || num2 > this.playerData.getPokemonList().size())
                return this.error("Invalid Pokemon number(s).");
            else if(num1 == num2) return this.error("You cannot breed a Pokemon with itself.");
            else if(PokeWorldNursery.getBreedingPairs(this.player.getId()).size() == MasteryLevelManager.getMaxNurseryPairs(this.playerData.getLevel()))
                return this.error("You have reached your max slots in the " + Pokeworld.NAME + " Nursery! You must wait for a pair to complete before you can breed more Pokemon.");

            Pokemon p1 = Pokemon.build(this.playerData.getPokemonList().get(num1 - 1), num1);
            Pokemon p2 = Pokemon.build(this.playerData.getPokemonList().get(num2 - 1), num2);
            if(p1 == null || p2 == null) return this.error();

            Function<String, Boolean> fail = msg -> this.error("**Breeding Failed**. Reason: " + msg);

            //No Eggs Egg Group can never breed
            if(p1.getEggGroups().contains(NO_EGGS)) return fail.apply(p1.getName() + " does not have any discovered eggs.");
            else if(p2.getEggGroups().contains(NO_EGGS)) return fail.apply(p2.getName() + " does not have any discovered eggs.");

            //Ditto can't breed with itself
            else if(p1.getEggGroups().contains(DITTO) && p2.getEggGroups().contains(DITTO)) return fail.apply("Ditto cannot breed with another Ditto.");

            //Non-Ditto-related Breeding Checks
            else if(!p1.getEggGroups().contains(DITTO) && !p2.getEggGroups().contains(DITTO))
            {
                //Unknown Gender can only breed with Ditto
                if(p1.getGender().equals(UNKNOWN)) return fail.apply(p1.getName() + " has an Unknown Gender.");
                else if(p2.getGender().equals(UNKNOWN)) return fail.apply(p2.getName() + " has an Unknown Gender.");

                //Manaphy and Phione can only breed with Ditto
                else if(p1.is(PokemonEntity.MANAPHY, PokemonEntity.PHIONE)) return fail.apply("Manaphy and Phione can only breed with Ditto.");
                else if(p2.is(PokemonEntity.MANAPHY, PokemonEntity.PHIONE)) return fail.apply("Manaphy and Phione can only breed with Ditto.");

                //Opposite Genders
                else if(p1.getGender().equals(p2.getGender())) return fail.apply(p1.getName() + " is the same Gender as " + p2.getName() + ".");

                //Must share an Egg Group
                EnumSet<EggGroup> setP1 = EnumSet.copyOf(p1.getEggGroups());
                setP1.retainAll(p2.getEggGroups());
                if(setP1.isEmpty()) return fail.apply(p1.getName() + " and " + p2.getName() + " do not share an Egg Group.");
            }

            //Final Checks

            //Not in cooldown
            if(PokeWorldNursery.isOnCooldown(p1)) return fail.apply(p1.getName() + " is currently on breeding cooldown. Cooldown ends " + PokeWorldNursery.getCooldownFormatted(p1.getUUID()) + ".");
            if(PokeWorldNursery.isOnCooldown(p2)) return fail.apply(p2.getName() + " is currently on breeding cooldown. Cooldown ends " + PokeWorldNursery.getCooldownFormatted(p2.getUUID()) + ".");

            //Valid Breeding Pair
            else
            {
                PokeWorldNursery.addBreedingPair(this.player.getId(), p1, p2);

                this.response = "You've added **" + p1.getName() + "** and **" + p2.getName() + "** to the " + Pokeworld.NAME + " Nursery! They will breed over time, and be removed from your Pokemon list until an Egg is created.";
            }
        }
        else if(subcommand.equals("nursery"))
        {
            List<PokeWorldNursery.BreedingPair> pairs = PokeWorldNursery.getBreedingPairs(this.player.getId());
            if(pairs.isEmpty()) return this.error("You do not have any Pokemon in the Nursery. Use `/breed pokemon` to breed your Pokemon!");

            this.embed.setTitle(Pokeworld.NAME + " Nursery – " + this.player.getName() + "'s Pokemon")
                    .setDescription("""
                            These are all your Pokemon currently in the Nursery.
                            Once the specified time passes, they will create a Pokemon Egg!
                            After Pokemon finish breeding, they will incur a cooldown until they can be put up for breeding again.
                            
                            **Nursery Usage**: `%s / %s`
                            """.formatted(pairs.size(), MasteryLevelManager.getMaxNurseryPairs(this.playerData.getLevel())));

            for(int i = 0; i < pairs.size(); i++)
            {
                PokeWorldNursery.BreedingPair pair = pairs.get(i);
                String title = "Breeding Pair " + (i + 1);
                String pokemon = "**" + pair.getPokemon1().getName() + "** and **" + pair.getPokemon2().getName() + "**";

                long epoch = Global.timeNowEpoch() + pair.getTime();
                String time = "*Egg* " + "<t:%s:R> (<t:%s:f>)".formatted(epoch, epoch);

                this.embed.addField(title, pokemon + "\n" + time, false);
            }
        }

        return true;
    }
}
