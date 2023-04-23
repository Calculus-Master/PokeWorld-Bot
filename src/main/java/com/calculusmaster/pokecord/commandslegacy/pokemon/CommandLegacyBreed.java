package com.calculusmaster.pokecord.commandslegacy.pokemon;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.commandslegacy.CommandLegacyInvalid;
import com.calculusmaster.pokecord.game.enums.elements.EggGroup;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.elements.Gender;
import com.calculusmaster.pokecord.game.objectives.ObjectiveType;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.evolution.PokemonEgg;
import com.calculusmaster.pokecord.util.enums.StatisticType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CommandLegacyBreed extends CommandLegacy
{
    public static final Vector<String> UNABLE_TO_BREED = new Vector<>();
    public static final Map<String, ScheduledFuture<?>> BREEDING_COOLDOWNS = new ConcurrentHashMap<>();

    public CommandLegacyBreed(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public CommandLegacy runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.BREED_POKEMON)) return this.invalidMasteryLevel(Feature.BREED_POKEMON);

        boolean breed = this.msg.length == 3 && this.isNumeric(1) && this.isNumeric(2);
        boolean cooldown = this.msg.length == 3 && this.msg[1].equals("cooldown") && this.isNumeric(2);

        if(breed)
        {
            int num1 = this.getInt(1);
            int num2 = this.getInt(2);

            if(num1 < 1 || num1 > this.playerData.getPokemonList().size() || num2 < 1 || num2 > this.playerData.getPokemonList().size()) this.response = "Invalid Pokemon number!";
            else if(num1 == num2) this.response = "You cannot breed a Pokemon with itself!";
            else
            {
                Pokemon parent1 = Pokemon.build(this.playerData.getPokemonList().get(num1 - 1));
                Pokemon parent2 = Pokemon.build(this.playerData.getPokemonList().get(num2 - 1));

                final String failed = "**Breeding Failed!**";

                boolean validEggGroup = parent1.getEggGroups().stream().anyMatch(e1 -> parent2.getEggGroups().stream().anyMatch(e1::equals));

                if(this.playerData.getOwnedEggIDs().size() >= PokemonEgg.MAX_EGGS) this.response = "You have the maximum number of eggs! To breed more, hatch some of your existing eggs!";
                else if(UNABLE_TO_BREED.contains(parent1.getUUID()) || UNABLE_TO_BREED.contains(parent2.getUUID())) this.response = failed + " Either " + parent1.getName() + " or " + parent2.getName() + " is on a breeding cooldown and cannot breed right now!";
                else if(parent1.getEggGroups().contains(EggGroup.NO_EGGS) || parent2.getEggGroups().contains(EggGroup.NO_EGGS)) this.response = failed + " Either " + parent1.getName() + " or " + parent2.getName() + " is part of the " + EggGroup.NO_EGGS.getName() + " Egg Group (and cannot breed)!";
                else if(parent1.is(PokemonEntity.DITTO) && parent2.is(PokemonEntity.DITTO)) this.response = failed + " Ditto cannot breed with itself!";
                else if(!validEggGroup && !parent1.is(parent2.getEntity()) && !parent1.is(PokemonEntity.DITTO) && !parent2.is(PokemonEntity.DITTO)) this.response = failed + " " + parent1.getName() + " and " + parent2.getName() + " do not share a common Egg Group and therefore cannot breed!";
                else if((!parent1.is(PokemonEntity.DITTO) && parent2.getGender().equals(Gender.UNKNOWN)) && (!parent2.is(PokemonEntity.DITTO) && parent1.getGender().equals(Gender.UNKNOWN))) this.response = failed + " Either " + parent1.getName() + " or " + parent2.getName() + " has an unknown gender and cannot breed!";
                else if(parent1.getGender().equals(parent2.getGender())) this.response = failed + " " + parent1.getName() + " and " + parent2.getName() + " are the same gender and cannot breed!";
                else
                {
                    PokemonEgg egg = PokemonEgg.create(parent1, parent2);
                    egg.upload();

                    this.playerData.addEgg(egg.getEggID());

                    this.playerData.updateObjective(ObjectiveType.BREED_POKEMON, 1);
                    this.playerData.getStatistics().increase(StatisticType.POKEMON_BRED);

                    this.startCooldown(parent1.getUUID());
                    this.startCooldown(parent2.getUUID());

                    this.response = parent1.getName() + " and " + parent2.getName() + " successfully bred and created an egg!";
                }
            }
        }
        else if(cooldown)
        {
            if(this.getInt(2) < 1 || this.getInt(2) > this.playerData.getPokemonList().size()) this.response = "Invalid number!";
            else
            {
                Pokemon p = Pokemon.build(this.playerData.getPokemonList().get(this.getInt(2) - 1));

                if(!UNABLE_TO_BREED.contains(p.getUUID()) && !BREEDING_COOLDOWNS.containsKey(p.getUUID())) this.response = p.getName() + " is able to breed!";
                else
                {
                    int rawSeconds = (int)BREEDING_COOLDOWNS.get(p.getUUID()).getDelay(TimeUnit.SECONDS);
                    int seconds = rawSeconds % 60;
                    int minutes = rawSeconds / 60;

                    this.response = p.getName() + " will be able to breed again in `" + minutes + " min " + seconds + " sec`!";
                }
            }
        }
        else this.response = CommandLegacyInvalid.getShort();

        return this;
    }

    private void startCooldown(String UUID)
    {
        UNABLE_TO_BREED.add(UUID);

        ScheduledFuture<?> cooldown = Executors.newScheduledThreadPool(1).schedule(() -> {
            UNABLE_TO_BREED.remove(UUID);
            BREEDING_COOLDOWNS.remove(UUID);
        }, 1, TimeUnit.HOURS);

        BREEDING_COOLDOWNS.put(UUID, cooldown);
    }

    public static void close()
    {
        BREEDING_COOLDOWNS.values().forEach(future -> future.cancel(true));
        BREEDING_COOLDOWNS.clear();
    }
}
