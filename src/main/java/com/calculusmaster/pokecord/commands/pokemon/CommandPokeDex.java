package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Ability;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.player.PlayerPokedex;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.evolution.*;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.FileUpload;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommandPokeDex extends PokeWorldCommand
{
    private static final int DEFAULT_POKEMON_PER_PAGE = 15; //TODO: Make this a client setting

    public static void init()
    {
        CommandData
                .create("pokedex")
                .withConstructor(CommandPokeDex::new)
                .withFeature(Feature.VIEW_DEX_INFO)
                .withCommand(Commands
                        .slash("pokedex", "View your PokeDex and information about specific Pokemon!")
                        .addSubcommands(
                                new SubcommandData("info", "View information about a specific Pokemon.")
                                        .addOption(OptionType.STRING, "name", "Name of the Pokemon.", true, true)
                                        .addOption(OptionType.BOOLEAN, "shiny", "Whether or not to display the Pokemon's Shiny form picture.", false, false),
                                new SubcommandData("view", "View your PokeDex and how many Pokemon you've collected.")
                                        .addOption(OptionType.INTEGER, "page", "Page number of your PokeDex.", false, false)
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        if(subcommand.equals("info"))
        {
            OptionMapping nameOption = Objects.requireNonNull(event.getOption("name"));
            OptionMapping shinyOption = event.getOption("shiny");

            PokemonEntity entity = PokemonEntity.cast(nameOption.getAsString());
            if(entity == null) return this.error("\"" + nameOption.getAsString() + "\" is not a valid Pokemon name.");

            PokemonData data = entity.data();
            boolean shiny = shinyOption != null && shinyOption.getAsBoolean();

            String generation = "Generation " + entity.getGeneration();
            String genus = data.getGenus().isEmpty() ? "Unknown Species" : data.getGenus();
            String flavorText = data.getFlavorText().isEmpty() ? "" : data.getFlavorText().get(this.random.nextInt(data.getFlavorText().size()));

            String height = BigDecimal.valueOf(data.getHeight()).stripTrailingZeros() + "m";
            String weight = BigDecimal.valueOf(data.getWeight()).stripTrailingZeros() + "kg";
            String type = data.getTypes().stream().map(Type::getStyledName).collect(Collectors.joining("\n"));

            String growthRate = Global.normalize(data.getGrowthRate().toString().replaceAll("_", " "));
            String expYield = String.valueOf(data.getBaseExperience());
            String evYield = data.getEVYield().get().entrySet().stream().filter(e -> e.getValue() > 0).map(e -> e.getValue() + " " + e.getKey().toString()).collect(Collectors.joining(", "));

            String eggGroups = data.getEggGroups().stream().map(eg -> Global.normalize(eg.toString().replaceAll("_", " "))).collect(Collectors.joining(", "));
            String genderRate = data.getGenderRate() == -1 ? "N/A" : (BigDecimal.valueOf(100 * data.getGenderRate() / 8.).stripTrailingZeros() + "% Male | " + BigDecimal.valueOf(100 * (8 - data.getGenderRate()) / 8.).stripTrailingZeros() + "% Female");
            String eggMoves = data.getEggMoves().isEmpty() ? "None" : data.getEggMoves().stream().map(e -> e.data().getName()).collect(Collectors.joining(", "));

            String mainAbilities = data.getMainAbilities().stream().map(Ability::getName).collect(Collectors.joining(", "));
            String hiddenAbilities = data.getHiddenAbilities().isEmpty() ? "None" : data.getHiddenAbilities().stream().map(Ability::getName).collect(Collectors.joining(", "));
            String tms = data.getTMs().isEmpty() ? "None" : data.getTMs().stream().filter(tm -> TM.cast(tm.data().getName()) != null).map(tm -> TM.cast(tm.data().getName()).toString()).collect(Collectors.joining(", "));

            String mega = "None";
            if(MegaEvolutionRegistry.isMega(entity)) mega = "Mega-Evolution of *" + MegaEvolutionRegistry.getData(entity).getBase().getName() + "*";
            else if(MegaEvolutionRegistry.hasMegaData(entity))
            {
                MegaEvolutionRegistry.MegaEvolutionData megaData = MegaEvolutionRegistry.getData(entity);
                if(megaData.isSingle()) mega = "*" + megaData.getMega().getName() + "*";
                else mega = "*" + megaData.getMegaX().getName() + "* or *" + megaData.getMegaY().getName() + "*";
            }
            String forms = FormRegistry.hasFormData(entity) ? FormRegistry.getFormData(entity).getForms().stream().filter(e -> e != entity).map(PokemonEntity::getName).collect(Collectors.joining(", ")) : "None";
            String evolution = "Does not evolve";
            if(EvolutionRegistry.hasEvolutionData(entity))
            {
                List<EvolutionData> evoData = EvolutionRegistry.getEvolutionData(entity);
                evolution = evoData.stream().map(eData -> "*" + eData.getTarget().getName() + "*").collect(Collectors.joining(" | "));
            }
            String gmax = GigantamaxRegistry.hasGMax(entity) ? "*Exists*" : "None";

            String baseStats = data.getBaseStats().get().entrySet().stream().map(e -> "**" + e.getKey().name + "**: " + e.getValue()).collect(Collectors.joining("\n"));

            this.embed
                    .setTitle("PokeDex Entry #" + entity.getDex() + ": " + entity.getName() + (shiny ? "🌟" : ""))
                    .setDescription("""
                            %s
                            __%s__
                            *%s*
                            """.formatted(generation, genus, flavorText))
                    .addField("Height", height, true)
                    .addField("Weight", weight, true)
                    .addField("Type", type, true)
                    .addField("Experience & Yields", """
                            **Growth Rate**: %s
                            **Experience Yield**: %s
                            **EV Yield**: %s
                            """.formatted(growthRate, expYield, evYield), false)
                    .addField("Breeding Information", """
                            **Egg Groups**: %s
                            **Gender Rate**: %s
                            **Egg Moves**: %s
                            """.formatted(eggGroups, genderRate, eggMoves), false)
                    .addField("Abilities & TMs", """
                            **Main Abilities**: %s
                            **Hidden Abilities**: %s
                            
                            **TMs**: %s
                            """.formatted(mainAbilities, hiddenAbilities, tms.isEmpty() ? "None" : tms), false)
                    .addField("Evolution & Forms", """
                            **Evolution**: %s
                            **Forms**: %s
                            **Mega-Evolution**: %s
                            **Gigantamax Form**: %s
                            """.formatted(evolution, forms, mega, gmax), false)
                    .addField("Base Stats", baseStats, false)
                    .setColor(data.getTypes().get(0).getColor())
            ;

            FileUpload image = this.setEmbedPokemonImage(Pokemon.getImage(entity, shiny, null, null), "dex_pokemon.png");
            event.replyFiles(image).setEmbeds(this.embed.build()).queue();
            this.embed = null;
        }
        else if(subcommand.equals("view"))
        {
            OptionMapping pageOption = event.getOption("page");

            PlayerPokedex dex = this.playerData.getPokedex();
            int page = pageOption == null ? 1 : pageOption.getAsInt();

            //Ensure page is within bounds
            int lastPage = PokemonEntity.values().length / DEFAULT_POKEMON_PER_PAGE + 1;
            if(page > lastPage) page = lastPage;
            else if(page < 1) page = 1;

            int startIndex = (page - 1) * DEFAULT_POKEMON_PER_PAGE;
            int endIndex = Math.min(PokemonEntity.values().length - 1, startIndex + DEFAULT_POKEMON_PER_PAGE - 1);

            if(startIndex > endIndex) startIndex = endIndex - DEFAULT_POKEMON_PER_PAGE + 1;

            List<MessageEmbed.Field> dexEntries = new ArrayList<>();
            for(int i = startIndex; i <= endIndex; i++)
            {
                PokemonEntity e = PokemonEntity.values()[i];

                String rarity = dex.hasCollected(e) ? Global.normalize(e.getRarity().toString()) : "Unknown";
                if(e.isNotSpawnable()) rarity += " 🚫";

                dexEntries.add(new MessageEmbed.Field(
                        e.getName() + (dex.hasCollected(e) ? " ✅" : " ❌"),
                        "#%s\nCollected: %s\nRarity: %s".formatted(e.getDex(), dex.getCollectedAmount(e), rarity),
                        true
                ));
            }

            int size = dex.getSize();
            int total = PokemonEntity.values().length;
            String percent = String.format("%.2f", (double)size / total * 100) + "%";
            String completionText = "%s (%s / %s Pokemon Discovered)".formatted(percent, size, total);

            String pageText = "Page %s / %s".formatted(page, lastPage);

            this.embed
                    .setTitle(this.player.getName() + "'s PokeDex")
                    .setDescription("""
                            Your PokeDex is a record of the Pokemon you've collected!
                            Discovering a new Pokemon rewards you credits, and you'll earn more credits each time you hit a collection milestone for a Pokemon.
                            
                            Once you collect a Pokemon, you will be able to see its Spawn Rarity below. Rarer Pokemon give more credits when reaching collection milestones!
                            Some Pokemon will never spawn randomly, and must be acquired through other means (these are designated with 🚫).
                            
                            Will you catch them all?
                            **PokeDex Completion: %s**
                            """.formatted(completionText))
                    .setFooter(pageText);

            dexEntries.forEach(f -> this.embed.addField(f));
            for(int i = 0; i < dexEntries.size() % 3; i++) this.embed.addBlankField(true);
        }

        return true;
    }

    @Override
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event)
    {
        if(event.getFocusedOption().getName().equals("name"))
        {
            String currentInput = event.getFocusedOption().getValue();

            List<String> pokemon = Arrays.stream(PokemonEntity.values()).map(PokemonEntity::getName).toList();

            event.replyChoiceStrings(this.getAutocompleteOptions(currentInput, pokemon)).queue();
        }

        return true;
    }
}
