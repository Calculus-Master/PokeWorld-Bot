package com.calculusmaster.pokecord.game.pokemon.sort;

import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugment;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.evolution.MegaEvolutionRegistry;
import com.calculusmaster.pokecord.game.world.PokeWorldMarket;
import com.calculusmaster.pokecord.mongo.PlayerData;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import kotlin.Pair;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.calculusmaster.pokecord.game.pokemon.sort.PokemonListOrderType.NUMBER;
import static com.calculusmaster.pokecord.game.pokemon.sort.PokemonListOrderType.TIME;

public class PokemonListSorter
{
    private final boolean market;

    private final List<Pokemon> originalList;
    private List<Pokemon> filteredList;

    private final List<String> query;
    private Pair<PokemonListOrderType, Boolean> sortType;
    private boolean hasNicknameQuery;

    private PlayerData playerData;

    public PokemonListSorter(boolean market, List<Pokemon> originalList, List<String> query)
    {
        this.market = market;

        this.originalList = originalList;
        this.filteredList = new ArrayList<>();

        this.query = query;
        this.sortType = null;
        this.hasNicknameQuery = false;

        this.playerData = null;
    }

    public PokemonListSorter withPlayerData(PlayerData playerData)
    {
        this.playerData = playerData;
        return this;
    }

    //Queries that are added as Slash Command Options
    //Pair Left: Queries | Right: Errors
    public static Pair<List<String>, List<String>> parsePriorityQueries(SlashCommandInteractionEvent event)
    {
        List<String> query = new ArrayList<>(), errors = new ArrayList<>();

        OptionMapping orderOption = event.getOption("order");
        OptionMapping descendingOption = event.getOption("descending");
        if(orderOption != null || descendingOption != null)
        {
            String order = orderOption == null ? "number" : orderOption.getAsString();
            boolean descending = descendingOption != null && descendingOption.getAsBoolean();
            query.add("order:" + order + ":" + (descending ? "descending" : "ascending"));
        }

        OptionMapping shinyOption = event.getOption("shiny");
        if(shinyOption != null) query.add(shinyOption.getAsBoolean() ? "is:shiny" : "-is:shiny");

        OptionMapping masteredOption = event.getOption("mastered");
        if(masteredOption != null) query.add(masteredOption.getAsBoolean() ? "is:mastered" : "-is:mastered");

        OptionMapping teamOption = event.getOption("team");
        if(teamOption != null) query.add(teamOption.getAsBoolean() ? "in:team" : "-in:team");

        OptionMapping favoritesOption = event.getOption("favorite");
        if(favoritesOption != null) query.add(favoritesOption.getAsBoolean() ? "in:favorites" : "-in:favorites");

        OptionMapping nameOption = event.getOption("name");
        if(nameOption != null && nameOption.getAsString().matches("[a-z0-9]+")) query.add("name:\"" + nameOption.getAsString() + "\"");
        else if(nameOption != null) errors.add("name:\"" + nameOption.getAsString() + "\"");

        OptionMapping nicknameOption = event.getOption("nickname");
        if(nicknameOption != null && nicknameOption.getAsString().matches("[a-z0-9]+")) query.add("nickname:\"" + nicknameOption.getAsString() + "\"");
        else if(nicknameOption != null) errors.add("name:\"" + nicknameOption.getAsString() + "\"");

        Function<String, String> numericOperatorParser = s -> s.replaceAll(":", "")
                .replaceAll("(>=)(?!:)(?<!:)", ":>=:")
                .replaceAll("(<=)(?!:)(?<!:)", ":<=:")
                .replaceAll("(>(?!=))(?!:)(?<!:)", ":>:")
                .replaceAll("(<(?!=))(?!:)(?<!:)", ":<:")
                .replaceAll("(=((?<!>)|(?!=>)))(?!:)(?<!:)", ":=:");

        OptionMapping statOption = event.getOption("stat");
        OptionMapping ivOption = event.getOption("iv");
        OptionMapping evOption = event.getOption("ev");
        OptionMapping levelOption = event.getOption("level");
        OptionMapping dynamaxLevelOption = event.getOption("dynamax-level");
        OptionMapping prestigeLevelOption = event.getOption("prestige-level");
        OptionMapping priceOption = event.getOption("price");

        if(statOption != null || ivOption != null || evOption != null || levelOption != null || dynamaxLevelOption != null || prestigeLevelOption != null || priceOption != null)
        {
            //Stat/IV/EV all need : attached since next option is the stat, the others dont since next option is the operator (which already adds a : )
            String header = statOption != null ? "stat:" : ivOption != null ? "iv:" : evOption != null ? "ev:" : levelOption != null ? "level" : dynamaxLevelOption != null ? "dynamax-level" : prestigeLevelOption != null ? "prestige-level" : "price";

            String content = statOption != null ? statOption.getAsString() : ivOption != null ? ivOption.getAsString() : evOption != null ? evOption.getAsString() : levelOption != null ? levelOption.getAsString() : dynamaxLevelOption != null ? dynamaxLevelOption.getAsString() : prestigeLevelOption != null ? prestigeLevelOption.getAsString() : priceOption.getAsString();

            content = numericOperatorParser.apply(content);

            query.add(header + (!content.contains(":") ? ":" : "") + content);
        }

        OptionMapping typeOption = event.getOption("type");
        if(typeOption != null) query.add("type:" + typeOption.getAsString());

        OptionMapping rarityOption = event.getOption("rarity");
        if(rarityOption != null) query.add("rarity:" + rarityOption.getAsString());

        OptionMapping natureOption = event.getOption("nature");
        if(natureOption != null) query.add("nature:" + natureOption.getAsString());

        OptionMapping eggGroupOption = event.getOption("egg-group");
        if(eggGroupOption != null) query.add("egg-group:" + eggGroupOption.getAsString());

        OptionMapping tmOption = event.getOption("tm");
        if(tmOption != null) query.add("tm:" + tmOption.getAsString());

        OptionMapping moveOption = event.getOption("move");
        if(moveOption != null) query.add("move:\"" + moveOption.getAsString() + "\"");

        OptionMapping abilityOption = event.getOption("ability");
        if(abilityOption != null) query.add("ability:" + abilityOption.getAsString());

        OptionMapping itemOption = event.getOption("item");
        if(itemOption != null) query.add("item:" + itemOption.getAsString());

        return new Pair<>(query, errors);
    }

    public List<String> filter()
    {
        List<Predicate<Pokemon>> filters = new ArrayList<>();
        List<String> erroredArgs = new ArrayList<>();

        LoggerHelper.info(PokemonListSorter.class, "Parsing Pokemon List Sorting Query: " + this.query);

        for(String argument : this.query)
        {
            boolean negate = argument.startsWith("-");
            String arg = (negate ? argument.substring(1) : argument).toLowerCase();

            Predicate<Pokemon> predicate = null;

            //Team, Favorites
            //in:team           in:favorites
            if(!this.market && arg.matches("^in:(team|favorites)$"))
            {
                if(arg.contains("team")) predicate = p -> this.playerData.getTeam().contains(p.getUUID());
                else predicate = p -> this.playerData.getFavorites().contains(p.getUUID());
            }
            //Stat, EV, IV
            //stat:hp:>=:100     ev:atk:<100     iv:spd:>=:100    stat:hp:100
            else if(arg.matches("^(stat|ev|iv):(hp|atk|def|spatk|spdef|spd|total):((<|=|>|>=|<=|):)?(\\d+)$"))
            {
                String[] split = arg.split(":");

                boolean isStat = split[0].equals("stat");
                boolean isEV = split[0].equals("ev");
                boolean isIV = split[0].equals("iv");

                Function<Pokemon, Integer> valueGetter;

                if(split[1].equals("total"))
                {
                    if(isStat) valueGetter = Pokemon::getTotalStat;
                    else if(isEV) valueGetter = Pokemon::getTotalEV;
                    else if(isIV) valueGetter = p -> (int)p.getTotalIVRounded();
                    else valueGetter = null;
                }
                else
                {
                    Stat stat = Stat.valueOf(split[1].toUpperCase());

                    if(isStat) valueGetter = p -> p.getStat(stat);
                    else if(isEV) valueGetter = p -> p.getEVs().get(stat);
                    else if(isIV) valueGetter = p -> p.getIVs().get(stat);
                    else valueGetter = null;
                }

                if(valueGetter != null)
                {
                    String operator = split.length == 3 ? "=" : split[2];
                    int number = Integer.parseInt(split.length == 3 ? split[2] : split[3]);

                    predicate = p -> {
                        int value = valueGetter.apply(p);

                        return switch(operator)
                        {
                            case "<" -> value < number;
                            case "=" -> value == number;
                            case ">" -> value > number;
                            case ">=" -> value >= number;
                            case "<=" -> value <= number;
                            default -> false;
                        };
                    };
                }
            }
            //Levels
            //level:>=:100       dynamax-level:100     prestige-level:<:100
            else if(arg.matches("^(level|dynamax-level|prestige-level|price):((<|=|>|>=|<=|):)?(\\d+)$"))
            {
                String[] split = arg.split(":");
                String type = split[0];

                Function<Pokemon, Integer> valueGetter = switch(type)
                {
                    case "level" -> Pokemon::getLevel;
                    case "dynamax-level" -> Pokemon::getDynamaxLevel;
                    case "prestige-level" -> Pokemon::getPrestigeLevel;
                    case "price" ->
                    {
                        if(this.market) yield p -> PokeWorldMarket.getMarketEntry(p).getPrice();
                        else yield null;
                    }
                    default -> null;
                };

                if(valueGetter != null)
                {
                    int number = Integer.parseInt(split.length == 2 ? split[1] : split[2]);
                    String operator = split.length == 2 ? "=" : split[1];

                    predicate = p -> {
                        int value = valueGetter.apply(p);

                        return switch(operator)
                        {
                            case "<" -> value < number;
                            case "=" -> value == number;
                            case ">" -> value > number;
                            case ">=" -> value >= number;
                            case "<=" -> value <= number;
                            default -> false;
                        };
                    };
                }
            }
            //Basic boolean filters of the form is:option
            else if(arg.matches("^is:(shiny|mastered|prestiged|legendary|mythical|ultrabeast|mega|mega-legendary|nicknamed)$"))
            {
                String[] split = arg.split(":");
                String option = split[1];

                predicate = switch(option)
                {
                    case "shiny" -> Pokemon::isShiny;
                    case "mastered" -> Pokemon::isMastered;
                    case "prestiged" -> p -> p.getPrestigeLevel() > 0;
                    case "legendary" -> p -> PokemonRarity.isLegendary(p.getEntity());
                    case "mythical" -> p -> PokemonRarity.isMythical(p.getEntity());
                    case "ultrabeast" -> p -> PokemonRarity.isUltraBeast(p.getEntity());
                    case "mega" -> p -> MegaEvolutionRegistry.isMega(p.getEntity());
                    case "mega-legendary" -> p -> MegaEvolutionRegistry.isMegaLegendary(p.getEntity());
                    case "nicknamed" -> Pokemon::hasNickname;
                    default -> p -> false;
                };
            }
            //Enums: Rarity, Type, Nature, Gender
            //rarity:<rarity>
            else if(arg.matches("^(rarity|type|maintype|nature|gender|egg-group|tm):[a-z0-9]+$"))
            {
                String[] split = arg.split(":");
                String enumType = split[0];

                switch(enumType)
                {
                    case "rarity" ->
                    {
                        PokemonRarity.Rarity rarity = PokemonRarity.Rarity.cast(split[1]);
                        if(rarity != null) predicate = p -> p.getRarity().equals(rarity);
                    }
                    case "type" ->
                    {
                        Type type = Type.cast(split[1]);
                        if(type != null) predicate = p -> p.isType(type);
                    }
                    case "maintype" ->
                    {
                        Type type = Type.cast(split[1]);
                        if(type != null) predicate = p -> p.getType().get(0).equals(type);
                    }
                    case "nature" ->
                    {
                        Nature nature = Nature.cast(split[1]);
                        if(nature != null) predicate = p -> p.getNature().equals(nature);
                    }
                    case "gender" ->
                    {
                        Gender gender = Gender.cast(split[1]);
                        if(gender != null) predicate = p -> p.getGender().equals(gender);
                    }
                    case "egg-group" ->
                    {
                        EggGroup eggGroup = EggGroup.cast(split[1]);
                        if(eggGroup != null) predicate = p -> p.getEggGroups().contains(eggGroup);
                    }
                    case "tm" ->
                    {
                        TM tm;
                        if(split[1].matches("\\d+"))
                        {
                            int num = Integer.parseInt(split[1]);
                            if(num >= 1 && num <= TM.values().length) tm = TM.values()[num - 1];
                            else tm = null;
                        }
                        else tm = TM.cast(split[1]);

                        if(tm != null) predicate = p -> p.hasTM() && p.getTM().equals(tm);
                    }
                }
            }
            //Name, Nickname, Ability, Move, Item, Augment
            else if(arg.matches("^(name|nickname|ability|move|item|augment):\"[a-z0-9 -]+\"$"))
            {
                String[] split = arg.split(":");
                String type = split[0];
                String contents = split[1].substring(1, split[1].length() - 1);

                if(type.equals("nickname")) this.hasNicknameQuery = true;

                predicate = switch(type)
                {
                    case "name" -> p -> p.getName().toLowerCase().contains(contents);
                    case "nickname" -> p -> p.hasNickname() && p.getNickname().toLowerCase().contains(contents);
                    case "ability" ->
                    {
                        Ability ability = Ability.cast(contents);
                        yield ability == null ? null : p -> p.getAbility().equals(ability);
                    }
                    case "move" ->
                    {
                        MoveEntity move = MoveEntity.cast(contents);
                        yield move == null ? null : p -> p.getMoves().stream().anyMatch(m -> m.equals(move));
                    }
                    case "item" ->
                    {
                        Item item = Item.cast(contents);
                        yield item == null ? null : p -> p.hasItem(item);
                    }
                    case "augment" ->
                    {
                        PokemonAugment augment = PokemonAugment.cast(contents);
                        yield augment == null ? null : p -> p.hasAugment(augment);
                    }
                    default -> p -> false;
                };
            }
            //Order
            else if(arg.matches("^order:[a-z]+(:(a|ascending|d|descending))?$"))
            {
                String[] split = arg.split(":");
                String type = split[1];

                PokemonListOrderType orderType = PokemonListOrderType.cast(type);
                if(orderType != null)
                {
                    boolean descending = split.length != 3 || (split[2].equals("d") || split[2].equals("descending"));
                    this.sortType = new Pair<>(orderType, descending);
                }

                predicate = p -> true;
            }
            //Pokemon has an item/tm
            //has:tm        has:item
            else if(arg.matches("^has:(item|tm)$"))
            {
                String[] split = arg.split(":");
                String type = split[1];

                predicate = switch(type)
                {
                    case "item" -> Pokemon::hasItem;
                    case "tm" -> Pokemon::hasTM;
                    default -> p -> false;
                };
            }

            //After parsing
            if(predicate != null) filters.add(negate ? predicate.negate() : predicate);
            //Didn't match any of the statements above
            else erroredArgs.add(argument);
        }

        this.filteredList = this.originalList.stream()
                .filter(filters
                        .stream()
                        .reduce(p -> true, Predicate::and))
                .collect(Collectors.toList());

        return erroredArgs;
    }

    public List<Pokemon> sort()
    {
        if(this.playerData != null); //setting

        if(this.sortType == null) //Default Sort Type
            this.sortType = this.market ? new Pair<>(TIME, true) : new Pair<>(NUMBER, false);

        //Default is Ascending
        switch(this.sortType.getFirst())
        {
            case NUMBER ->
            {
                if(!this.market)
                    this.filteredList.sort(Comparator.comparingInt(Pokemon::getNumber));
            }
            case IV -> this.filteredList.sort(Comparator.comparingDouble(Pokemon::getTotalIVRounded));
            case EV -> this.filteredList.sort(Comparator.comparingInt(Pokemon::getTotalEV));
            case STAT -> this.filteredList.sort(Comparator.comparingInt(Pokemon::getTotalStat));
            case LEVEL -> this.filteredList.sort(Comparator.comparingInt(Pokemon::getLevel));
            case NAME -> this.filteredList.sort(Comparator.comparing(Pokemon::getName));
            case RANDOM -> Collections.shuffle(this.filteredList);
            case PRICE ->
            {
                if(this.market)
                    this.filteredList.sort(Comparator.comparingInt((p -> PokeWorldMarket.getMarketEntry(p).getPrice())));
            }
            case TIME ->
            {
                if(this.market)
                    this.filteredList.sort(Comparator.comparingLong((p -> PokeWorldMarket.getMarketEntry(p).getTimestamp())));
            }
        }

        if(this.sortType.getSecond()) Collections.reverse(this.filteredList);

        return this.filteredList;
    }

    public Pair<PokemonListOrderType, Boolean> getSortType()
    {
        return this.sortType;
    }

    public boolean hasNicknameQuery()
    {
        return this.hasNicknameQuery;
    }
}
