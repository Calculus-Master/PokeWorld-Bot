package com.calculusmaster.pokecord.commands.player;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.functional.Achievement;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.mongo.PlayerData;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class CommandStart extends PokeWorldCommand
{
    private static final String STARTERS_IMAGE = "https://vignette.wikia.nocookie.net/pokeverse/images/4/46/Pokemon_starters_.png/revision/latest/scale-to-width-down/1000?cb=20180424013225";

    public static void init()
    {
        CommandData
                .create("start")
                .withConstructor(CommandStart::new)
                .withCommand(Commands
                        .slash("start", "Start your adventure!")
                        .addOption(OptionType.STRING, "starter", "The starter you want to begin your journey with.", false, true)
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        if(PlayerData.isRegistered(this.player.getId())) return this.error("You have already started your journey.");

        OptionMapping starterOption = event.getOption("starter");

        //Info Embed
        if(starterOption == null)
        {
            this.embed
                    .setTitle("Welcome to the world of Pokemon!")
                    .setDescription("""
                            %s is a Discord bot that lets you catch, battle, trade, and do many other things with Pokemon!
                            It uses a level-based progression system, and you'll unlock more and more features as you level up (more information below).
                            
                            *But, before all that, select a Starter to begin your journey!*
                            """.formatted(Pokeworld.NAME))
                    .addField("Starter", """
                            You may select any of the following Pokemon to be your starter:
                            Generation 1: **Bulbasaur** | **Charmander** | **Squirtle**
                            Generation 2: **Chikorita** | **Cyndaquil** | **Totodile**
                            Generation 3: **Treecko** | **Torchic** | **Mudkip**
                            Generation 4: **Turtwig** | **Chimchar** | **Piplup**
                            Generation 5: **Snivy** | **Tepig** | **Oshawott**
                            Generation 6: **Chespin** | **Fennekin** | **Froakie**
                            Generation 7: **Rowlet** | **Litten** | **Popplio**
                            Generation 8: **Grookey** | **Scorbunny** | **Sobble**
                            Generation 9: **Sprigatito** | **Fuecoco** | **Quaxly**
                            
                            *Once you've decided who you'll pick, use `/start` again, with the name of the starter you chose, to begin!*
                            """, false)
                    .addField("Progression", """
                            This bot uses a Level-based progression system.
                            To unlock certain features, you have to earn enough experience and complete certain tasks in order to advance to the next level.
                            
                            After selecting a starter, you'll receive a DM with more information about Pokemon Mastery Level!
                            """, false)
                    .addField("Help", """
                            If you're ever feeling stuck, take a closer look at the information provided on each slash command.
                            More complicated systems will have built-in tutorial systems, primarily in the form of a subcommand called "tutorial".
                            
                            The Pokemon Mastery Level system will also send you a DM with more information about newly unlocked features, as you level up!
                            """, false)
                    .setImage(STARTERS_IMAGE);
        }
        //Starter Selected - Start the Player
        else
        {
            String rawStarterName = starterOption.getAsString();
            PokemonEntity starterEntity = PokemonEntity.cast(rawStarterName);

            if(starterEntity == null) return this.error("\"%s\" is not a valid starter name. Please check your spelling.".formatted(rawStarterName));
            else
            {
                //Registering Player
                PlayerData.register(this.player);
                DataHelper.addServerPlayer(this.server, this.player);

                this.playerData = PlayerData.build(this.player.getId());

                //Creating the Starter
                Pokemon starter = Pokemon.create(starterEntity);
                starter.setLevel(5);

                Map<Stat, Integer> starterIVs = new LinkedHashMap<>();
                Random r = new Random();
                for(Stat s : Stat.values()) starterIVs.put(s, r.nextInt(22, 30));
                starter.setIVs(starterIVs);

                Achievement.START.grant(this.playerData, () -> true, event.getChannel().asTextChannel());

                //Registering the Starter
                starter.upload();
                this.playerData.addPokemon(starter.getUUID());

                this.playerData.getPokedex().add(starter.getEntity());
                this.playerData.updatePokedex();

                //Next Steps
                this.playerData.dmMasteryLevel();

                this.response = "You started your journey with **" + starter.getName() + "**! Check your DMs for more information about where to head next, and welcome to " + Pokeworld.NAME + "!";
            }
        }

        return true;
    }

    @Override
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event)
    {
        if(event.getFocusedOption().getName().equals("starter"))
            event.replyChoiceStrings(this.getAutocompleteOptions(event.getFocusedOption().getValue(), Global.STARTERS.stream().map(PokemonEntity::getName).toList())).queue();

        return true;
    }
}
