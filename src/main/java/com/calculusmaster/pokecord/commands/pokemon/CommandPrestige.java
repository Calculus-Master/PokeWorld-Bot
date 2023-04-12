package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.util.enums.Prices;
import com.calculusmaster.pokecord.util.enums.StatisticType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Objects;

public class CommandPrestige extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("prestige")
                .withConstructor(CommandPrestige::new)
                .withFeature(Feature.PRESTIGE_POKEMON)
                .withCommand(Commands
                        .slash("prestige", "Prestige your Pokemon to permanently boost their stats!")
                        .addSubcommands(
                                new SubcommandData("guide", "Mini-Tutorial: Learn how to prestige your Pokemon."),
                                new SubcommandData("advance", "Prestige your active Pokemon.")
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        if(subcommand.equals("guide"))
        {
            this.embed
                    .setTitle("Prestige: A Short Guide")
                    .setDescription("""
                            Prestige is a system where you can reset a Pokemon's level and experience back to 0 in order to gain permanent stat bonuses.
                            These bonuses are based on the number of times a Pokemon has been prestiged, and can be viewed using `/info`.
                            """)
                    .addField("Prestige: Requirements & the Process", """
                            In order to prestige your Pokemon, it must be **Level 100** and **not at its maximum Prestige Level**. (You can see the maximum Prestige Level in `/info`).
                            
                            Once your Pokemon meets the requirements, simply use `/prestige advance` to prestige it.
                            Its *level*, *experience*, *moves* and *augments* will be reset, but, it'll get permanent boosts to its stats!
                            """, false)
                    .addField("Prestige Levels & Bonuses", """
                            Every Pokemon has a maximum Prestige Level, determined by its rarity.
                            Most Pokemon can prestige multiple times, but the rarest Pokemon can only prestige once.
                            
                            The bonuses you get from prestiging a Pokemon are based on its Prestige Level.
                            There are 3 different stat multipliers: for Health (HP), Speed (SPD), and a common multiplier for the other 4 stats (Attack/Defense/Special Attack/Special Defense).
                            """, false);
        }
        else if(subcommand.equals("advance"))
        {
            Pokemon active = this.playerData.getSelectedPokemon();

            if(active.getPrestigeLevel() == active.getMaxPrestigeLevel()) return this.error(active.getName() + " is already at their maximum prestige level. You cannot prestige it any further.");
            else if(active.getLevel() < 100) return this.error(active.getName() + " must be at Level 100 in order to be able to prestige.");
            else if(this.playerData.getCredits() < Prices.PRESTIGE.get()) return this.error("You do not have enough credits to prestige " + active.getName() + ". You need " + Prices.PRESTIGE.get() + "c.");

            this.playerData.changeCredits(-Prices.PRESTIGE.get());

            active.increasePrestigeLevel();
            active.updatePrestigeLevel();

            active.setLevel(1); active.setExp(0);
            active.updateExperience();

            active.setMoves(); active.updateMoves();

            active.clearAugments(); active.updateAugments();

            this.playerData.getStatistics().increase(StatisticType.POKEMON_PRESTIGED);

            this.response = "**" + active.getName() + "** has advanced to **Prestige Level " + active.getPrestigeLevel() + "**! Its stats have been permanently boosted.\n*Additionally, its level, experience, moves and augments have been reset.*";
        }

        return true;
    }
}
