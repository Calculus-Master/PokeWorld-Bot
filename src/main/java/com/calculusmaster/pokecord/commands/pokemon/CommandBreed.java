package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.enums.elements.EggGroup;
import com.calculusmaster.pokecord.game.enums.elements.Gender;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.PokemonEgg;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CommandBreed extends Command
{
    public static final List<String> UNABLE_TO_BREED = new ArrayList<>();

    public CommandBreed(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        boolean breed = this.msg.length == 3 && this.isNumeric(1) && this.isNumeric(2);

        if(breed)
        {
            int num1 = this.getInt(1);
            int num2 = this.getInt(2);

            if(num1 < 1 || num1 > this.playerData.getPokemonList().size() || num2 < 1 || num2 > this.playerData.getPokemonList().size()) this.sendMsg("Invalid Pokemon number!");
            else if(num1 == num2) this.sendMsg("You cannot breed a Pokemon with itself!");
            else
            {
                Pokemon parent1 = Pokemon.build(this.playerData.getPokemonList().get(num1 - 1));
                Pokemon parent2 = Pokemon.build(this.playerData.getPokemonList().get(num2 - 1));

                final String failed = "**Breeding Failed!**";

                boolean validEggGroup = false;
                for(EggGroup g1 : parent1.getEggGroup()) for(EggGroup g2 : parent2.getEggGroup()) if(g1.equals(g2)) validEggGroup = true;

                if(Collections.synchronizedList(UNABLE_TO_BREED).contains(parent1.getUUID()) || Collections.synchronizedList(UNABLE_TO_BREED).contains(parent2.getUUID())) this.sendMsg(failed + " Either " + parent1.getName() + " or " + parent2.getName() + " is on a breeding cooldown and cannot breed right now!");
                else if(parent1.getEggGroup().contains(EggGroup.NO_EGGS) || parent2.getEggGroup().contains(EggGroup.NO_EGGS)) this.sendMsg(failed + " Either " + parent1.getName() + " or " + parent2.getName() + " is part of the " + EggGroup.NO_EGGS.getName() + " Egg Group (and cannot breed)!");
                else if(parent1.getName().equals("Ditto") && parent2.getName().equals("Ditto")) this.sendMsg(failed + " Ditto cannot breed with itself!");
                else if(!validEggGroup && !parent1.getName().equals(parent2.getName()) && !parent1.getName().equals("Ditto") && !parent2.getName().equals("Ditto")) this.sendMsg(failed + " " + parent1.getName() + " and " + parent2.getName() + " do not share a common Egg Group and therefore cannot breed!");
                else if((!parent1.getName().equals("Ditto") && parent2.getGender().equals(Gender.UNKNOWN)) && (!parent2.getName().equals("Ditto") && parent1.getGender().equals(Gender.UNKNOWN))) this.sendMsg(failed + " Either " + parent1.getName() + " or " + parent2.getName() + " has an unknown gender and cannot breed!");
                else if(parent1.getGender().equals(parent2.getGender())) this.sendMsg(failed + " " + parent1.getName() + " and " + parent2.getName() + " are not opposite genders and cannot breed!");
                else
                {
                    PokemonEgg egg = PokemonEgg.create(parent1, parent2);
                    PokemonEgg.toDB(egg);

                    this.playerData.addEgg(egg.getEggID());

                    this.playerData.updateBountyProgression(ObjectiveType.BREED_POKEMON);

                    Achievements.grant(this.player.getId(), Achievements.BRED_FIRST_POKEMON, this.event);
                    if(parent1.getName().equals("Ditto") || parent2.getName().equals("Ditto")) Achievements.grant(this.player.getId(), Achievements.BRED_FIRST_DITTO, this.event);
                    if(parent1.getGender().equals(Gender.UNKNOWN) || parent2.getGender().equals(Gender.UNKNOWN)) Achievements.grant(this.player.getId(), Achievements.BRED_FIRST_UNKNOWN, this.event);

                    Collections.synchronizedList(UNABLE_TO_BREED).add(parent1.getUUID());
                    Collections.synchronizedList(UNABLE_TO_BREED).add(parent2.getUUID());

                    Executors.newScheduledThreadPool(1).schedule(() -> Collections.synchronizedList(UNABLE_TO_BREED).remove(parent1.getUUID()), 1, TimeUnit.HOURS);
                    Executors.newScheduledThreadPool(1).schedule(() -> Collections.synchronizedList(UNABLE_TO_BREED).remove(parent2.getUUID()), 1, TimeUnit.HOURS);

                    this.sendMsg(parent1.getName() + " and " + parent2.getName() + " successfully bred and created an egg!");
                }
            }
        }
        else this.sendMsg(CommandInvalid.getShort());

        return this;
    }
}
