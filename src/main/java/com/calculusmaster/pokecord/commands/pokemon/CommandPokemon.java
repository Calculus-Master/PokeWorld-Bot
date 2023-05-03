package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.sort.PokemonListOrderType;
import com.calculusmaster.pokecord.game.pokemon.sort.PokemonListSorter;
import com.calculusmaster.pokecord.util.Global;
import kotlin.Pair;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static com.calculusmaster.pokecord.game.pokemon.sort.PokemonListOrderType.*;

public class CommandPokemon extends PokeWorldCommand
{
    public static final int POKEMON_PER_PAGE = 20;

    public static void init()
    {
        CommandData
                .create("pokemon")
                .withConstructor(CommandPokemon::new)
                .withFeature(Feature.VIEW_POKEMON_LIST)
                .withCommand(Commands
                        .slash("pokemon", "View your Pokemon!")
                        .addOption(OptionType.INTEGER, "page", "The page of your Pokemon list to view.", false)
                        .addOption(OptionType.STRING, "custom", "Add a custom filter query for your Pokemon list.", false)
                        //Popular queries - made into option types
                        .addOption(OptionType.STRING, "order", "The order to sort Pokemon in. (Default: by number)", false)
                        .addOption(OptionType.BOOLEAN, "descending", "Whether to sort Pokemon in descending order. (Default: ascending)", false)
                        .addOption(OptionType.BOOLEAN, "shiny", "Filter Pokemon by whether they are shiny or not.", false)
                        .addOption(OptionType.BOOLEAN, "mastered", "Filter Pokemon by whether they are mastered or not.", false)
                        .addOption(OptionType.BOOLEAN, "team", "Filter Pokemon by whether they are on your team or not.", false)
                        .addOption(OptionType.BOOLEAN, "favorite", "Filter Pokemon by whether they are on your favorites list or not.", false)
                        .addOption(OptionType.STRING, "name", "Filter Pokemon by name.", false)
                        .addOption(OptionType.STRING, "nickname", "Filter Pokemon by nickname.", false)
                        .addOption(OptionType.STRING, "stat", "Filter Pokemon by stat values.", false)
                        .addOption(OptionType.STRING, "iv", "Filter Pokemon by IV values.", false)
                        .addOption(OptionType.STRING, "ev", "Filter Pokemon by EV values.", false)
                        .addOption(OptionType.STRING, "type", "Filter Pokemon by Type.", false, true)
                        .addOption(OptionType.STRING, "rarity", "Filter Pokemon by Rarity.", false, true)
                        .addOption(OptionType.STRING, "nature", "Filter Pokemon by Nature.", false, true)
                        .addOption(OptionType.STRING, "egg-group", "Filter Pokemon by Egg Group.", false, true)
                        .addOption(OptionType.STRING, "tm", "Filter Pokemon by TM.", false, true)
                        .addOption(OptionType.STRING, "move", "Filter Pokemon by a move it knows.", false, true)
                        .addOption(OptionType.STRING, "ability", "Filter Pokemon by Ability.", false, true)
                        .addOption(OptionType.STRING, "item", "Filter Pokemon by held Item.", false, true)
                        .addOption(OptionType.STRING, "level", "Filter Pokemon by level.", false)
                        .addOption(OptionType.STRING, "dynamax-level", "Filter Pokemon by Dynamax Level.", false)
                        .addOption(OptionType.STRING, "prestige-level", "Filter Pokemon by Prestige Level.", false)
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();

        OptionMapping customOption = event.getOption("custom");

        List<Pokemon> pokemonList = this.playerData.getPokemon();
        List<String> query = customOption == null ? new ArrayList<>() : new ArrayList<>(List.of(customOption.getAsString().split("\\s(?=(([^\"]*\"){2})*[^\"]*$)\\s*")));

        //Popular query (that are option types) parsed here
        Pair<List<String>, List<String>> parsedOptions = PokemonListSorter.parsePriorityQueries(event);
        query.addAll(parsedOptions.getFirst());
        List<String> errors = new ArrayList<>(parsedOptions.getSecond());

        //Create sorter
        PokemonListSorter sorter = new PokemonListSorter(false, pokemonList.stream().distinct().toList(), query).withPlayerData(this.playerData);

        //Filter
        errors.addAll(sorter.filter());

        if(!errors.isEmpty())
            this.playerData.directMessage("*Your recent `/pokemon` command had some errors. The following inputs were invalid*:\n```\n" + String.join("\n", errors) + "\n```");

        //Sort
        List<Pokemon> pokemon = sorter.sort();

        if(pokemon.isEmpty()) return this.error("None of your Pokemon matched your given filters.");

        //Creating Embed
        OptionMapping pageOption = event.getOption("page");
        int maxPage = 1 + pokemon.size() / POKEMON_PER_PAGE;
        int page = pageOption == null || pageOption.getAsInt() < 0 ? 1 : Math.min(pageOption.getAsInt(), maxPage);

        int startIndex = (page - 1) * POKEMON_PER_PAGE;
        int endIndex = Math.min(startIndex + POKEMON_PER_PAGE, pokemon.size());

        List<String> pokemonHeaders = new ArrayList<>(), pokemonLevels = new ArrayList<>(), pokemonInfo = new ArrayList<>();
        PokemonListOrderType sortType = sorter.getSortType().getFirst();
        boolean tagsSeparate = !EnumSet.of(STAT, IV, EV).contains(sortType);

        for(int i = startIndex; i < endIndex; i++)
        {
            Pokemon p = pokemon.get(i);

            List<String> tags = new ArrayList<>();
            if(p.isShiny()) tags.add(":star2:");
            if(p.getPrestigeLevel() != 0) tags.add(":zap:");
            if(this.playerData.getTeam().contains(p.getUUID())) tags.add(":regional_indicator_t:");
            if(this.playerData.getFavorites().contains(p.getUUID())) tags.add(":regional_indicator_f:");

            pokemonHeaders.add("`#%s` **%s**%s".formatted(
                    p.getNumber(),
                    p.getDisplayName() + (sorter.hasNicknameQuery() && p.hasNickname() ? " (" + p.getName() + ")" : ""),
                    tagsSeparate ? "" : " " + String.join("", tags)
            ));

            pokemonLevels.add("`%s`".formatted(p.getLevel()));

            if(sortType.equals(STAT)) pokemonInfo.add("`%s`".formatted(String.valueOf(p.getTotalStat())));
            else if(sortType.equals(IV)) pokemonInfo.add("`%s`".formatted(p.getTotalIV()));
            else if(sortType.equals(EV)) pokemonInfo.add("`%s`".formatted(String.valueOf(p.getTotalEV())));
            else pokemonInfo.add(tags.isEmpty() ? "`None`" : String.join("", tags));
        }

        String footer = """
                Sorted by: %s (%s)
                Total Pokemon: %s (Filter: %s)
                """.formatted(
                        sortType.getName(), sorter.getSortType().getFirst().equals(RANDOM) ? "N/A" : sorter.getSortType().getSecond() ? "Descending" : "Ascending",
                        pokemonList.size(), pokemon.size());

        this.embed
                .setTitle(this.player.getName() + "'s Pokemon")
                .setDescription("""
                        Page **%s** of **%s**
                        """.formatted(page, maxPage))

                .addField("Pokemon", String.join("\n", pokemonHeaders), true)
                .addField("Level", String.join("\n", pokemonLevels), true)
                .addField(sortType.equals(STAT) ? "Total Stat" : sortType.equals(IV) ? "Total IV" : sortType.equals(EV) ? "Total EV" : "Tags", String.join("\n", pokemonInfo), true)

                .setFooter(footer);

        return true;
    }

    @Override
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event)
    {
        String option = event.getFocusedOption().getName();

        List<String> sourceList = switch(option)
        {
            case "type" -> Arrays.stream(Type.values()).map(Type::getStyledName).toList();
            case "rarity" -> Arrays.stream(PokemonRarity.Rarity.values()).map(PokemonRarity.Rarity::getName).toList();
            case "nature" -> Arrays.stream(Nature.values()).map(n -> Global.normalize(n.toString())).toList();
            case "egg-group" -> Arrays.stream(EggGroup.values()).map(EggGroup::getName).toList();
            case "tm" -> Arrays.stream(TM.values()).map(TM::toString).toList();
            case "move" -> Arrays.stream(MoveEntity.values()).map(MoveEntity::getName).toList();
            case "ability" -> Arrays.stream(Ability.values()).map(Ability::getName).toList();
            case "item" -> Arrays.stream(Item.values()).map(Item::getStyledName).toList();
            default -> new ArrayList<>();
        };

        String input = event.getFocusedOption().getValue();
        if(!sourceList.isEmpty()) event.replyChoiceStrings(this.getAutocompleteOptions(input, sourceList)).queue();

        return true;
    }
}
